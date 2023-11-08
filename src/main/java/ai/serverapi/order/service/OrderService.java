package ai.serverapi.order.service;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.member.domain.Member;
import ai.serverapi.order.domain.Orders;
import ai.serverapi.order.domain.OrdersDetail;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.ReceiptResponse;
import ai.serverapi.order.dto.response.TempOrderResponse;
import ai.serverapi.order.dto.response.TempOrderVo;
import ai.serverapi.order.repository.OrdersDetailRepostiory;
import ai.serverapi.order.repository.OrdersRepository;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.enums.Status;
import ai.serverapi.product.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        List<TempOrderDto> requestOrderList = tempOrderRequest.getOrderList();
        Map<Long, Integer> productEaMap = new HashMap<>();

        for (TempOrderDto t : requestOrderList) {
            productEaMap.put(t.getProductId(), t.getEa());
        }

        List<Long> orderProductIdList = requestOrderList.stream()
                                                        .map(o -> o.getProductId())
                                                        .collect(Collectors.toList());

        List<Product> productList = productRepository.findAllById(orderProductIdList);

        if (requestOrderList.size() != productList.size()) {
            throw new IllegalArgumentException("유효하지 않은 상품 id가 존재합니다.");
        }

        List<Product> notNormalProductList = productList.stream()
                                                        .filter(p -> !p.getStatus()
                                                                       .equals(Status.NORMAL))
                                                        .toList();

        if (!notNormalProductList.isEmpty()) {
            for (Product p : notNormalProductList) {
                log.error("[order] [tempOrder] [유효하지 않은 상품 주문 시도] member = {}, id = {}, name = {}",
                    member.getId(), p.getId(), p.getMainTitle());
            }
            throw new IllegalArgumentException("유효하지 않은 상품이 존재합니다.");
        }

        List<ReceiptResponse> receiptList = new ArrayList<>();
        List<TempOrderVo> orderList = new ArrayList<>();
        int totalPrice = 0;

        Orders orders = Orders.of(member);
        Orders saveOrder = ordersRepository.save(orders);

        for (Product p : productList) {
            int ea = productEaMap.get(p.getId());

            OrdersDetail ordersDetail = OrdersDetail.of(orders, p, ea);
            ordersDetailRepository.save(ordersDetail);

            totalPrice += ordersDetail.getProductTotalPrice();

            receiptList.add(ReceiptResponse.of(ordersDetail));

            orderList.add(TempOrderVo.of(ordersDetail));
        }

        return new TempOrderResponse(saveOrder.getId(), totalPrice, receiptList, orderList);
    }
}
