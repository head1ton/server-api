package ai.serverapi.order.service;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.repository.SellerJpaRepository;
import ai.serverapi.order.domain.entity.DeliveryEntity;
import ai.serverapi.order.domain.entity.OrderEntity;
import ai.serverapi.order.domain.entity.OrderItemEntity;
import ai.serverapi.order.domain.vo.OrderVo;
import ai.serverapi.order.dto.request.CompleteOrderRequest;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.CompleteOrderResponse;
import ai.serverapi.order.dto.response.OrderResponse;
import ai.serverapi.order.dto.response.PostTempOrderResponse;
import ai.serverapi.order.dto.response.TempOrderResponse;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.order.repository.DeliveryJpaRepository;
import ai.serverapi.order.repository.OrderCustomJpaRepositoryImpl;
import ai.serverapi.order.repository.OrderItemJpaRepository;
import ai.serverapi.order.repository.OrderJpaRepository;
import ai.serverapi.product.domain.entity.OptionEntity;
import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.domain.entity.SellerEntity;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import ai.serverapi.product.repository.ProductJpaRepository;
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

    private final SellerJpaRepository sellerJpaRepository;

    private final MemberUtil memberUtil;
    private final ProductJpaRepository productJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository ordersDetailRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final DeliveryJpaRepository deliveryJpaRepository;
    private final OrderCustomJpaRepositoryImpl orderCustomRepositoryImpl;

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
        MemberEntity memberEntity = memberUtil.getMember(request);

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

        List<ProductEntity> productEntityList = productJpaRepository.findAllById(
            requestProductIdList);

        sb.append(productEntityList.get(0).getMainTitle());

        if (requestOrderProductList.size() > 1) {
            sb.append(" 외 ");
            sb.append(requestOrderProductList.size() - 1);
            sb.append("개");
        }
        OrderEntity saveOrderEntity = orderJpaRepository.save(
            OrderEntity.of(memberEntity, sb.toString()));

        // 주문 상품 등록
        Map<Long, ProductEntity> eaMap = new HashMap<>();
        for (ProductEntity p : productEntityList) {
            eaMap.put(p.getId(), p);
        }

        for (TempOrderDto tod : requestOrderProductList) {
            ProductEntity p = eaMap.get(tod.getProductId());

            if (p.getStatus() != ProductStatus.NORMAL) {
                throw new IllegalArgumentException("상품 상태가 유효하지 않습니다.");
            }

            Long optionId = tod.getOptionId();

            // 옵션이 없으 ㄹ경우 exception 처리
            OptionEntity optionEntity =
                p.getType() == ProductType.OPTION ? p.getOptionList().stream().filter(
                                                                     o -> o.getId().equals(optionId))
                                                     .findFirst().orElseThrow(
                    () -> new IllegalArgumentException("optionId가 유효하지 않습니다.")) : null;

            // 재고 확인
            int ea = tod.getEa();
            int productEa = p.getType() == ProductType.OPTION ? optionEntity.getEa() : p.getEa();
            checkEa(p.getMainTitle(), productEa, ea);

            OrderItemEntity orderItemEntity = orderItemJpaRepository.save(
                OrderItemEntity.of(saveOrderEntity, p, optionEntity, ea));
            saveOrderEntity.getOrderItemList().add(orderItemEntity);

        }

        return PostTempOrderResponse.builder()
                                    .orderId(saveOrderEntity.getId())
                                    .build();
    }

    @Override
    public TempOrderResponse getTempOrder(Long orderId, HttpServletRequest request) {
        /**
         * order id 와 member 정보로 임시 정보를 불러옴
         */
        OrderEntity orderEntity = checkOrder(orderId, request);

        return TempOrderResponse.from(orderEntity);
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
        OrderEntity orderEntity = checkOrder(orderId, request);

        List<OrderItemEntity> orderItemEntityList = orderEntity.getOrderItemList();
        for (OrderItemEntity oi : orderItemEntityList) {
            ProductEntity productEntity = oi.getProduct();

            int orderEa = oi.getEa();
            ProductType productType = productEntity.getType();
            int productEa = 0;
            OptionEntity optionEntity = null;

            if (productType == ProductType.OPTION) {
                optionEntity = Optional.ofNullable(oi.getOption()).orElseThrow(
                    () -> new IllegalArgumentException("요청한 주문의 optionId가 유효하지 않습니다."));
                OptionEntity constOptionEntity = optionEntity;
                productEa = productEntity.getOptionList().stream()
                                         .filter(o -> o.getId().equals(constOptionEntity.getId()))
                                         .findFirst().orElseThrow(
                        () -> new IllegalArgumentException("optionId 가 유효하지 않습니다.")).getEa();
            } else {
                productEa = productEntity.getEa();
            }

            checkEa(productEntity.getMainTitle(), productEa, orderEa);

            productEntity.minusEa(orderEa, optionEntity);
        }

        // 배송 정보 등록
        for (OrderItemEntity oi : orderItemEntityList) {
            deliveryJpaRepository.save(DeliveryEntity.of(orderEntity, oi, completeOrderRequest));
            oi.statusComplete();
        }

        orderEntity.statusComplete();

        LocalDateTime createdAt = orderEntity.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String orderNumber = String.format("ORDER-%s-%s", createdAt.format(formatter), orderId);
        orderEntity.orderNumber(orderNumber);

        return CompleteOrderResponse.from(orderEntity);
    }

    @NonNull
    private OrderEntity checkOrder(final Long orderId, final HttpServletRequest request) {
        MemberEntity memberEntity = memberUtil.getMember(request);
        OrderEntity orderEntity = orderJpaRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 주문 번호입니다."));

        if (memberEntity.getId() != orderEntity.getMember().getId()) {
            log.info("[getOrder] 유저가 주문한 번호가 아님! 요청한 user_id = {}, order_id = {}",
                memberEntity.getId(),
                orderEntity.getId());
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }

        if (orderEntity.getStatus() != OrderStatus.TEMP) {
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }
        return orderEntity;
    }

    @Override
    public OrderResponse getOrderListBySeller(Pageable pageable, String search, String status,
        HttpServletRequest request) {
        MemberEntity memberEntity = memberUtil.getMember(request);
        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity)
                                                       .orElseThrow(() -> new UnauthorizedException(
                                                           "잘못된 접근입니다."));

        /**
         * 1. order item 중 seller product 가 있는 리스트 불러오기
         * 2. response data 만들기
         */
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));

        Page<OrderVo> orderList = orderCustomRepositoryImpl.findAllBySeller(pageable, search,
            orderStatus, sellerEntity);

        return OrderResponse.from(orderList);
    }
}
