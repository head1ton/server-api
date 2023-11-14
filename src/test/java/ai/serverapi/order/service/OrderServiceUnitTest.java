package ai.serverapi.order.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Disabled
@ExtendWith({MockitoExtension.class})
class OrderServiceUnitTest {

//    @InjectMocks
//    private OrderServiceImpl orderServiceImpl;
//    @Mock
//    private MemberUtil memberUtil;
//    @Mock
//    private ProductJpaRepository productJpaRepository;
//    @Mock
//    private OrderJpaRepository orderJpaRepository;
//    @Mock
//    private OrderItemJpaRepository orderItemJpaRepository;
//    @Mock
//    private DeliveryJpaRepository deliveryJpaRepository;
//    @Mock
//    private SellerJpaRepository sellerJpaRepository;
//    private final MockHttpServletRequest request = new MockHttpServletRequest();
//
//    @Test
//    @DisplayName("임시 주문 불러오기에 유효하지 않은 order id로 실패")
//    void getTempOrderFail1() {
//        given(orderJpaRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));
//
//        assertThatThrownBy(() -> orderServiceImpl.getOrderInfo(0L, request))
//            .isInstanceOf(IllegalArgumentException.class)
//            .hasMessageContaining("유효하지 않은 주문 번호");
//    }
//
//    @Test
//    @DisplayName("임시 주문 불러오기에 주문을 요청한 member id가 아닌 경우 실패")
//    void getTempOrderFail2() {
//        Long orderId = 1L;
//        Long memberId = 1L;
//        LocalDateTime now = LocalDateTime.now();
//        MemberEntity memberEntity1 = new MemberEntity(memberId, "email@gmail.com", "password",
//            "nickname", "name",
//            "19991030",
//            MemberRole.SELLER, null, null, now, now);
//        MemberEntity memberEntity2 = new MemberEntity(2L, "email@gmail.com", "password", "nickname",
//            "name",
//            "19991030",
//            MemberRole.SELLER, null, null, now, now);
//        given(memberUtil.getMember(any())).willReturn(memberEntity1);
//
//        given(orderJpaRepository.findById(anyLong())).willReturn(Optional.of(
//            OrderEntity.from(Order.builder()
//                                  .id(orderId)
//                                  .member(memberEntity2.toModel())
//                                  .status(OrderStatus.TEMP)
//                                  .createdAt(now)
//                                  .modifiedAt(now)
//                                  .build())
//        ));
//
//        assertThatThrownBy(() -> orderServiceImpl.getOrderInfo(orderId, request))
//            .isInstanceOf(IllegalArgumentException.class)
//            .hasMessageContaining("유효하지 않은 주문");
//    }
//
//    @Test
//    @DisplayName("주문 자체에 유효하지 않은 option id값으로 인해 주문 완료 실패")
//    void completeOrderFail1() {
//        Long orderId = 1L;
//        LocalDateTime now = LocalDateTime.now();
//        MemberEntity memberEntity = new MemberEntity(1L, "email@gmail.com", "password", "nickname",
//            "name",
//            "19991030", MemberRole.SELLER, null, null,
//            now, now);
//        CompleteOrderRequest completeOrderRequest = CompleteOrderRequest.builder()
//                                                                        .orderId(orderId)
//                                                                        .ownerName("주문자")
//                                                                        .ownerZonecode("1234567")
//                                                                        .ownerAddress("주문자 주소")
//                                                                        .ownerAddressDetail(
//                                                                            "주문자 상세 주소")
//                                                                        .ownerTel("주문자 연락처")
//                                                                        .recipientName("수령인")
//                                                                        .recipientZonecode(
//                                                                            "1234567")
//                                                                        .recipientAddress("수령인 주소")
//                                                                        .recipientAddressDetail(
//                                                                            "수령인 상세 주소")
//                                                                        .recipientTel("수령인 연락처")
//                                                                        .build();
//
//        ProductEntity productEntity = ProductEntity.builder()
//                                                   .id(PRODUCT_ID_MASK)
//                                                   .mainTitle("상품명1")
//                                                   .price(10000)
//                                                   .status(ProductStatus.NORMAL)
//                                                   .ea(10)
//                                                   .build();
//
//        OrderEntity orderEntity = OrderEntity.from(Order.builder()
//                                                        .id(orderId)
//                                                        .member(memberEntity.toModel())
//                                                        .status(OrderStatus.TEMP)
//                                                        .createdAt(now)
//                                                        .modifiedAt(now)
//                                                        .build());
//
//        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
////        OrderItemEntity option1 = OrderItemEntity.of(orderEntity, productEntity,
////            new OptionEntity("option1", 1000, 100, productEntity), 1);
////        orderItemEntityList.add(option1);
//
//        given(orderJpaRepository.findById(anyLong())).willReturn(Optional.ofNullable(orderEntity));
//        given(memberUtil.getMember(any())).willReturn(memberEntity);
//
//        CompleteOrderResponse completeOrderResponse = orderServiceImpl.completeOrder(
//            completeOrderRequest, request);
//
//        assertThat(completeOrderResponse.getOrderNumber()).contains("ORDER-");
//    }
//
//    @Test
//    @DisplayName("주문 완료 성공")
//    void completeOrderSuccess() {
//        Long orderId = 1L;
//        LocalDateTime now = LocalDateTime.now();
//        MemberEntity memberEntity = new MemberEntity(1L, "email@gmail.com", "password", "nickname",
//            "name",
//            "19991030", MemberRole.SELLER, null, null, now, now);
//
//        CompleteOrderRequest completeOrderRequest = CompleteOrderRequest.builder()
//                                                                        .orderId(orderId)
//                                                                        .ownerName("주문자")
//                                                                        .ownerZonecode("1234567")
//                                                                        .ownerAddress("주문자 주소")
//                                                                        .ownerAddressDetail(
//                                                                            "주문자 상세 주소")
//                                                                        .ownerTel("주문자 연락처")
//                                                                        .recipientName("수령인")
//                                                                        .recipientZonecode(
//                                                                            "1234567")
//                                                                        .recipientAddress("수령인 주소")
//                                                                        .recipientAddressDetail(
//                                                                            "수령인 상세 주소")
//                                                                        .recipientTel("수령인 연락처")
//                                                                        .build();
//
//        given(orderJpaRepository.findById(anyLong())).willReturn(Optional.ofNullable(
//            OrderEntity.from(Order.builder()
//                                  .id(orderId)
//                                  .member(memberEntity.toModel())
//                                  .status(OrderStatus.TEMP)
//                                  .createdAt(now)
//                                  .modifiedAt(now)
//                                  .build())
//        ));
//
//        given(memberUtil.getMember(any())).willReturn(memberEntity);
//
//        CompleteOrderResponse completeOrderResponse = orderServiceImpl.completeOrder(
//            completeOrderRequest, request);
//
//        assertThat(completeOrderResponse.getOrderNumber()).contains("ORDER-");
//    }
//
//    @Test
//    @DisplayName("seller가 아닌 관리자 주문 내역 불러오기 실패")
//    void getOrderListBySellerFail1() {
//        Long memberId = 1L;
//        LocalDateTime now = LocalDateTime.now();
//        MemberEntity memberEntity1 = new MemberEntity(memberId, "email@gmail.com", "password",
//            "nickname", "name",
//            "19991030", MemberRole.SELLER, null, null, now, now);
//
//        given(memberUtil.getMember(any())).willReturn(memberEntity1);
//        given(sellerJpaRepository.findByMember(any(MemberEntity.class))).willReturn(
//            Optional.ofNullable(null));
//
//        assertThatThrownBy(
//            () -> orderServiceImpl.getOrderListBySeller(Pageable.ofSize(10), "", "COMPLETE",
//                request))
//            .isInstanceOf(UnauthorizedException.class)
//            .hasMessageContaining("잘못된 접근");
//    }
}
