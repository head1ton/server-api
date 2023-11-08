package ai.serverapi.order.service;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.member.domain.Member;
import ai.serverapi.order.domain.Orders;
import ai.serverapi.order.domain.OrdersDetail;
import ai.serverapi.order.dto.request.TempOrder;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.TempOrderResponse;
import ai.serverapi.order.repository.OrdersDetailRepostiory;
import ai.serverapi.order.repository.OrdersRepository;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final MemberUtil memberUtil;
    private final ProductRepository productRepository;
    private final OrdersRepository ordersRepository;
    private final OrdersDetailRepostiory ordersDetailRepository;

    @Transactional
    public TempOrderResponse postTempOrder(
        TempOrderRequest tempOrderRequest,
        HttpServletRequest request
    ) {
        Member member = memberUtil.getMember(request);

        List<TempOrder> requestOrderList = tempOrderRequest.getOrderList();
        Map<Long, Integer> productEaMap = new HashMap<>();

        for (TempOrder t : requestOrderList) {
            productEaMap.put(t.getProductId(), t.getEa());
        }

        List<Long> orderProductIdList = requestOrderList.stream()
                                                        .map(o -> o.getProductId())
                                                        .collect(Collectors.toList());

        List<Product> productList = productRepository.findAllById(orderProductIdList);

        if (requestOrderList.size() != productList.size()) {
            throw new IllegalArgumentException("유효하지 않은 상품 id가 존재합니다.");
        }

        Orders orders = Orders.of(member);
        Orders saveOrder = ordersRepository.save(orders);

        for (Product p : productList) {
            Integer intValue = productEaMap.get(p.getId());
            if (intValue == null) {
                throw new IllegalArgumentException("유효하지 않은 상품 id가 존재합니다.");
            }
            OrdersDetail ordersDetail = OrdersDetail.of(orders, p, intValue);
            ordersDetailRepository.save(ordersDetail);
        }

//        TempOrderResponse tempOrderResponse = new TempOrderResponse(saveOrder.getId());

        return null;
    }
}
