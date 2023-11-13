package ai.serverapi.order.service;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.order.domain.Delivery;
import ai.serverapi.order.domain.Order;
import ai.serverapi.order.domain.OrderItem;
import ai.serverapi.order.dto.request.CompleteOrderRequest;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.CompleteOrderResponse;
import ai.serverapi.order.dto.response.OrderResponse;
import ai.serverapi.order.dto.response.OrderVo;
import ai.serverapi.order.dto.response.PostTempOrderResponse;
import ai.serverapi.order.dto.response.TempOrderResponse;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.order.repository.DeliveryRepository;
import ai.serverapi.order.repository.OrderCustomRepositoryImpl;
import ai.serverapi.order.repository.OrderItemRepository;
import ai.serverapi.order.repository.OrderRepository;
import ai.serverapi.product.domain.Option;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import ai.serverapi.product.repository.ProductRepository;
import com.github.dockerjava.api.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final SellerRepository sellerRepository;

    private final MemberUtil memberUtil;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository ordersDetailRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderCustomRepositoryImpl orderCustomRepositoryImpl;

    private static void checkEa(final String productName, final int productEa, final int ea) {
        if (productEa < ea) {
            throw new IllegalArgumentException(
                String.format("[%s]상품의 재고가 부족합니다.! 남은 재고 = %s개", productName, productEa));
        }
    }

    @Transactional
    @Override
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

        sb.append(productList.get(0).getMainTitle());

        if (requestOrderProductList.size() > 1) {
            sb.append(" 외 ");
            sb.append(requestOrderProductList.size() - 1);
            sb.append("개");
        }
        Order saveOrder = orderRepository.save(Order.of(member, sb.toString()));

        // 주문 상품 등록
        Map<Long, Product> eaMap = new HashMap<>();
        for (Product p : productList) {
            eaMap.put(p.getId(), p);
        }

        for (TempOrderDto tod : requestOrderProductList) {
            Product p = eaMap.get(tod.getProductId());

            if (p.getStatus() != ProductStatus.NORMAL) {
                throw new IllegalArgumentException("상품 상태가 유효하지 않습니다.");
            }

            Long optionId = tod.getOptionId();

            // 옵션이 없으 ㄹ경우 exception 처리
            Option option = p.getType() == ProductType.OPTION ? p.getOptionList().stream().filter(
                                                                     o -> o.getId().equals(optionId))
                                                                 .findFirst().orElseThrow(
                    () -> new IllegalArgumentException("optionId가 유효하지 않습니다.")) : null;

            // 재고 확인
            int ea = tod.getEa();
            int productEa = p.getType() == ProductType.OPTION ? option.getEa() : p.getEa();
            checkEa(p.getMainTitle(), productEa, ea);

            OrderItem orderItem = orderItemRepository.save(OrderItem.of(saveOrder, p, option, ea));
            saveOrder.getOrderItemList().add(orderItem);

        }

        return PostTempOrderResponse.builder()
                                    .orderId(saveOrder.getId())
                                    .build();
    }

    @Override
    public TempOrderResponse getTempOrder(Long orderId, HttpServletRequest request) {
        /**
         * order id 와 member 정보로 임시 정보를 불러옴
         */
        Order order = checkOrder(orderId, request);

        return TempOrderResponse.from(order);
    }

    @Transactional
    @Override
    public CompleteOrderResponse completeOrder(
        CompleteOrderRequest completeOrderRequest,
        HttpServletRequest request) {
        /**
         * 주문 정보 update
         * 1. 주문 상태 변경
         * 2. 주문자, 수령자 정보 등록
         * 3. 주문 번호 만들기
         */
        Long orderId = completeOrderRequest.getOrderId();
        Order order = checkOrder(orderId, request);

        List<OrderItem> orderItemList = order.getOrderItemList();
        for (OrderItem oi : orderItemList) {
            Product product = oi.getProduct();

            int orderEa = oi.getEa();
            ProductType productType = product.getType();
            int productEa = 0;
            Option option = null;

            if (productType == ProductType.OPTION) {
                option = Optional.ofNullable(oi.getOption()).orElseThrow(
                    () -> new IllegalArgumentException("요청한 주문의 optionId가 유효하지 않습니다."));
                Option constOption = option;
                productEa = product.getOptionList().stream()
                                   .filter(o -> o.getId().equals(constOption.getId()))
                                   .findFirst().orElseThrow(
                        () -> new IllegalArgumentException("optionId 가 유효하지 않습니다.")).getEa();
            } else {
                productEa = product.getEa();
            }

            checkEa(product.getMainTitle(), productEa, orderEa);

            product.minusEa(orderEa, option);
        }

        // 배송 정보 등록
        for (OrderItem oi : orderItemList) {
            deliveryRepository.save(Delivery.of(order, oi, completeOrderRequest));
            oi.statusComplete();
        }

        order.statusComplete();

        LocalDateTime createdAt = order.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String orderNumber = String.format("ORDER-%s-%s", createdAt.format(formatter), orderId);
        order.orderNumber(orderNumber);

        return CompleteOrderResponse.from(order);
    }

    @NonNull
    private Order checkOrder(final Long orderId, final HttpServletRequest request) {
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
        return order;
    }

    @Override
    public OrderResponse getOrderListBySeller(Pageable pageable, String search, String status,
        HttpServletRequest request) {
        Member member = memberUtil.getMember(request);
        Seller seller = sellerRepository.findByMember(member)
                                        .orElseThrow(() -> new UnauthorizedException("잘못된 접근입니다."));

        /**
         * 1. order item 중 seller product 가 있는 리스트 불러오기
         * 2. response data 만들기
         */
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));

        Page<OrderVo> orderList = orderCustomRepositoryImpl.findAllBySeller(pageable, search,
            orderStatus, seller);

        return OrderResponse.from(orderList);
    }
}
