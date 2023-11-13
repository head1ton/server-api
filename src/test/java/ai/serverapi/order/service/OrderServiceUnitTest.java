package ai.serverapi.order.service;

import static ai.serverapi.Base.PRODUCT_ID_MASK;
import static ai.serverapi.Base.PRODUCT_ID_PEAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.enums.Role;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.order.domain.Order;
import ai.serverapi.order.domain.OrderItem;
import ai.serverapi.order.dto.request.CompleteOrderRequest;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.CompleteOrderResponse;
import ai.serverapi.order.dto.response.PostTempOrderResponse;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.order.repository.DeliveryRepository;
import ai.serverapi.order.repository.OrderItemRepository;
import ai.serverapi.order.repository.OrderRepository;
import ai.serverapi.product.domain.Option;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import ai.serverapi.product.repository.ProductRepository;
import com.github.dockerjava.api.exception.UnauthorizedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith({MockitoExtension.class})
class OrderServiceUnitTest {

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;
    @Mock
    private MemberUtil memberUtil;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private SellerRepository sellerRepository;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품이 존재해 실패")
    void postTempOrderFail2() {
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_MASK)
                                                 .ea(5)
                                                 .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_PEAR)
                                                 .ea(10)
                                                 .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);

        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = Product.builder()
                                  .id(PRODUCT_ID_MASK)
                                  .ea(10)
                                  .build();
        Product product2 = Product.builder()
                                  .id(PRODUCT_ID_PEAR)
                                  .ea(10)
                                  .build();
        productList.add(product1);
        productList.add(product2);

        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(
            new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        assertThatThrownBy(() -> orderServiceImpl.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("상품 상태");
    }

    @Test
    @DisplayName("옵션 번호가 유효하지 않아 상품 임시 주문 등록에 실패")
    void postTempOrderFail3() {
        //given
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_MASK)
                                                 .ea(10)
                                                 .optionId(300L)
                                                 .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_PEAR)
                                                 .ea(5)
                                                 .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = Product.builder()
                                  .id(PRODUCT_ID_MASK)
                                  .mainTitle("상품명1")
                                  .price(10000)
                                  .ea(10)
                                  .status(ProductStatus.NORMAL)
                                  .type(ProductType.OPTION)
                                  .build();
        Product product2 = Product.builder()
                                  .id(PRODUCT_ID_PEAR)
                                  .mainTitle("상품명2")
                                  .price(10000)
                                  .ea(10)
                                  .status(ProductStatus.NORMAL)
                                  .build();
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(
            new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        //when
        //then
        assertThatThrownBy(() -> orderServiceImpl.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("optionId");
    }

    @Test
    @DisplayName("상품 재고가 부족하여 상품 임시 주문 등록에 실패")
    void postTempOrderFail4() {
        //given
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_MASK)
                                                 .ea(20)
                                                 .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_PEAR)
                                                 .ea(5)
                                                 .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = Product.builder()
                                  .id(PRODUCT_ID_MASK)
                                  .mainTitle("상품명1")
                                  .price(10000)
                                  .ea(10)
                                  .status(ProductStatus.NORMAL)
                                  .build();
        Product product2 = Product.builder()
                                  .id(PRODUCT_ID_PEAR)
                                  .mainTitle("상품명2")
                                  .price(10000)
                                  .ea(10)
                                  .status(ProductStatus.NORMAL)
                                  .build();
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(
            new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        //when
        //then
        assertThatThrownBy(() -> orderServiceImpl.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고");
    }

    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_MASK)
                                                 .ea(10)
                                                 .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_PEAR)
                                                 .ea(5)
                                                 .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = Product.builder()
                                  .id(PRODUCT_ID_MASK)
                                  .mainTitle("상품명1")
                                  .price(10000)
                                  .ea(10)
                                  .status(ProductStatus.NORMAL)
                                  .build();
        Product product2 = Product.builder()
                                  .id(PRODUCT_ID_PEAR)
                                  .mainTitle("상품명2")
                                  .price(10000)
                                  .ea(10)
                                  .status(ProductStatus.NORMAL)
                                  .build();
        productList.add(product1);
        productList.add(product2);

        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(
            new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        PostTempOrderResponse postTempOrderResponse = orderServiceImpl.postTempOrder(
            tempOrderRequest,
            request);

        assertThat(postTempOrderResponse.getOrderId()).isNotNull();

    }

    @Test
    @DisplayName("임시 주문 불러오기에 유효하지 않은 order id로 실패")
    void getTempOrderFail1() {
        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        assertThatThrownBy(() -> orderServiceImpl.getTempOrder(0L, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 주문 번호");
    }

    @Test
    @DisplayName("임시 주문 불러오기에 주문을 요청한 member id가 아닌 경우 실패")
    void getTempOrderFail2() {
        Long orderId = 1L;
        Long memberId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Member member1 = new Member(memberId, "email@gmail.com", "password", "nickname", "name",
            "19991030",
            Role.SELLER, null, null, now, now);
        Member member2 = new Member(2L, "email@gmail.com", "password", "nickname", "name",
            "19991030",
            Role.SELLER, null, null, now, now);
        given(memberUtil.getMember(any())).willReturn(member1);
        given(orderRepository.findById(anyLong())).willReturn(
            Optional.ofNullable(new Order(orderId, member2, null, new ArrayList<>(), null,
                OrderStatus.TEMP, "", now, now)));

        assertThatThrownBy(() -> orderServiceImpl.getTempOrder(orderId, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 주문");
    }

    @Test
    @DisplayName("임시 주문 불러오기에 주문 status가 temp가 아닌 경우 실패")
    void getTempOrderFail3() {
        Long orderId = 1L;
        Long memberId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Member member1 = new Member(memberId, "email@gmail.com", "password", "nickname", "name",
            "19991030", Role.SELLER, null, null, now, now);

        given(memberUtil.getMember(any())).willReturn(member1);
        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(
            new Order(orderId, member1, null, new ArrayList<>(), null, OrderStatus.ORDER, "",
                now, now)));

        assertThatThrownBy(() -> orderServiceImpl.getTempOrder(orderId, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 주문");
    }

    @Test
    @DisplayName("주문 자체에 유효하지 않은 option id값으로 인해 주문 완료 실패")
    void completeOrderFail1() {
        Long orderId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@gmail.com", "password", "nickname", "name",
            "19991030", Role.SELLER, null, null,
            now, now);
        CompleteOrderRequest completeOrderRequest = CompleteOrderRequest.builder()
                                                                        .orderId(orderId)
                                                                        .ownerName("주문자")
                                                                        .ownerZonecode("1234567")
                                                                        .ownerAddress("주문자 주소")
                                                                        .ownerAddressDetail(
                                                                            "주문자 상세 주소")
                                                                        .ownerTel("주문자 연락처")
                                                                        .recipientName("수령인")
                                                                        .recipientZonecode(
                                                                            "1234567")
                                                                        .recipientAddress("수령인 주소")
                                                                        .recipientAddressDetail(
                                                                            "수령인 상세 주소")
                                                                        .recipientTel("수령인 연락처")
                                                                        .build();

        Product product = Product.builder()
                                 .id(PRODUCT_ID_MASK)
                                 .mainTitle("상품명1")
                                 .price(10000)
                                 .status(ProductStatus.NORMAL)
                                 .ea(10)
                                 .build();

        Order order = new Order(orderId, member, null, new ArrayList<>(), null, OrderStatus.TEMP,
            "", now, now);
        List<OrderItem> orderItemList = order.getOrderItemList();
        orderItemList.add(
            OrderItem.of(order, product, new Option("option1", 1000, 100, product), 1));

        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(order));
        given(memberUtil.getMember(any())).willReturn(member);

        CompleteOrderResponse completeOrderResponse = orderServiceImpl.completeOrder(
            completeOrderRequest, request);

        assertThat(completeOrderResponse.getOrderNumber()).contains("ORDER-");
    }

    @Test
    @DisplayName("주문 완료 성공")
    void completeOrderSuccess() {
        Long orderId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@gmail.com", "password", "nickname", "name",
            "19991030", Role.SELLER, null, null, now, now);

        CompleteOrderRequest completeOrderRequest = CompleteOrderRequest.builder()
                                                                        .orderId(orderId)
                                                                        .ownerName("주문자")
                                                                        .ownerZonecode("1234567")
                                                                        .ownerAddress("주문자 주소")
                                                                        .ownerAddressDetail(
                                                                            "주문자 상세 주소")
                                                                        .ownerTel("주문자 연락처")
                                                                        .recipientName("수령인")
                                                                        .recipientZonecode(
                                                                            "1234567")
                                                                        .recipientAddress("수령인 주소")
                                                                        .recipientAddressDetail(
                                                                            "수령인 상세 주소")
                                                                        .recipientTel("수령인 연락처")
                                                                        .build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(
            new Order(orderId, member, null, new ArrayList<>(), null, OrderStatus.TEMP, "", now,
                now)));

        given(memberUtil.getMember(any())).willReturn(member);

        CompleteOrderResponse completeOrderResponse = orderServiceImpl.completeOrder(
            completeOrderRequest, request);

        assertThat(completeOrderResponse.getOrderNumber()).contains("ORDER-");
    }

    @Test
    @DisplayName("seller가 아닌 관리자 주문 내역 불러오기 실패")
    void getOrderListBySellerFail1() {
        Long memberId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Member member1 = new Member(memberId, "email@gmail.com", "password", "nickname", "name",
            "19991030", Role.SELLER, null, null, now, now);

        given(memberUtil.getMember(any())).willReturn(member1);
        given(sellerRepository.findByMember(any(Member.class))).willReturn(
            Optional.ofNullable(null));

        assertThatThrownBy(
            () -> orderServiceImpl.getOrderListBySeller(Pageable.ofSize(10), "", "COMPLETE",
                request))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("잘못된 접근");
    }
}
