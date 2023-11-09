package ai.serverapi.order.service;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.member.domain.Member;
import ai.serverapi.order.domain.Order;
import ai.serverapi.order.domain.OrderItem;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.PostTempOrderResponse;
import ai.serverapi.order.dto.response.TempOrderResponse;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.order.repository.OrderItemRepository;
import ai.serverapi.order.repository.OrderRepository;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.enums.Status;
import ai.serverapi.product.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final OrderRepository orderRepository;
    private final OrderItemRepository ordersDetailRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public PostTempOrderResponse postTempOrder(
        TempOrderRequest tempOrderRequest,
        HttpServletRequest request
    ) {
        Member member = memberUtil.getMember(request);

        /**
         * 1. 주문 발급
         * 2. 주문 상품 등록
         * 3. 판매자 리스트 등록
         */

        // 주문 발급
        StringBuffer sb = new StringBuffer();
        List<TempOrderDto> requestOrderProductList = tempOrderRequest.getOrderList();
        List<Long> requestProductIdList = requestOrderProductList.stream().mapToLong(
                                                                     TempOrderDto::getProductId)
                                                                 .boxed().toList();

        List<Product> productList = productRepository.findAllById(requestProductIdList);
        productList = productList.stream().sorted((p1, p2) -> p2.getPrice() - p1.getPrice())
                                 .toList();

        if ((requestOrderProductList.size() != productList.size()) || productList.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 상품이 존재합니다.");
        }

        sb.append(productList.get(0).getMainTitle());
        if (productList.size() > 1) {
            sb.append(" 외 ");
            sb.append(productList.size() - 1);
            sb.append("개");
        }
        Order saveOrder = orderRepository.save(Order.of(member, sb.toString()));

        // 주문 상품 등록
        Map<Long, Integer> eaMap = new HashMap<>();
        for (TempOrderDto o : requestOrderProductList) {
            eaMap.put(o.getProductId(), o.getEa());
        }

        for (Product p : productList) {
            if (p.getStatus() != Status.NORMAL) {
                throw new IllegalArgumentException("상품 상태가 유효하지 않습니다.");
            }

            int ea = eaMap.get(p.getId());
            OrderItem orderItem = orderItemRepository.save(OrderItem.of(saveOrder, p, ea));
            saveOrder.getOrderItemList().add(orderItem);

        }

        return new PostTempOrderResponse(saveOrder.getId());
    }

    public TempOrderResponse getTempOrder(Long orderId, HttpServletRequest request) {
        /**
         * order id 와 member 정보로 임시 정보를 불러옴
         */
        Member member = memberUtil.getMember(request);
        Order order = orderRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 주문 번호입니다."));

        if (member.getId() != order.getMember().getId()) {
            log.info("[getOrder] 유저가 주문한 번호가 아님! 요청한 user_id = {}, order_id = {}", member.getId(),
                order.getId());
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }

        if (order.getStatus() != OrderStatus.TEMP) {
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }

        return TempOrderResponse.of(order);
    }
}
