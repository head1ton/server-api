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
import ai.serverapi.order.domain.Order;
import ai.serverapi.order.dto.request.CompleteOrderRequest;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.CompleteOrderResponse;
import ai.serverapi.order.dto.response.PostTempOrderResponse;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.order.repository.OrderItemRepository;
import ai.serverapi.order.repository.OrderRepository;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.repository.ProductRepository;
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
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith({MockitoExtension.class})
class OrderServiceUnitTest {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private MemberUtil memberUtil;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품 id로 인해 실패")
    void postTempOrderFail1() {
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_MASK)
                                                 .ea(3)
                                                 .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_PEAR)
                                                 .ea(10)
                                                 .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품");
    }

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
        Product product1 = new Product(1L, null, null, null, null, null, null, 0, 0, null, null,
            null, null, null, null, null, null, ProductStatus.NORMAL, 10, null, null, null, null);
        Product product2 = new Product(1L, null, null, null, null, null, null, 0, 0, null, null,
            null, null, null, null, null, null, ProductStatus.HIDDEN, 10, null, null, null, null);
        productList.add(product1);
        productList.add(product2);

        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(
            new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("상품 상태");
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

        Product product1 = new Product(1L, null, null, "상품명1", null, null, null, 0, 10000, null,
            null, null, null, null, null, null, null, ProductStatus.NORMAL, 10, null, null, null, null);
        Product product2 = new Product(1L, null, null, "상품명2", null, null, null, 0, 12000, null,
            null, null, null, null, null, null, null, ProductStatus.NORMAL, 10, null, null, null, null);
        productList.add(product1);
        productList.add(product2);

        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(
            new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        PostTempOrderResponse postTempOrderResponse = orderService.postTempOrder(tempOrderRequest,
            request);

        assertThat(postTempOrderResponse.getOrderId()).isNotNull();

    }

    @Test
    @DisplayName("임시 주문 불러오기에 유효하지 않은 order id로 실패")
    void getTempOrderFail1() {
        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        assertThatThrownBy(() -> orderService.getTempOrder(0L, request))
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

        assertThatThrownBy(() -> orderService.getTempOrder(orderId, request))
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

        assertThatThrownBy(() -> orderService.getTempOrder(orderId, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 주문");
    }

    @Test
    @DisplayName("주문 완료 성공")
    void completeOrderSuccess() {
        Long orderId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@gmail.com", "password", "nickname", "name",
            "19991030", Role.SELLER, null, null, now, now);

        CompleteOrderRequest completeOrderRequest = new CompleteOrderRequest(orderId, "주문자",
            "주문자 우편번호", "주문자 주소", "주문자 상세 주소", "주문자 연락처", "수령인", "수령인 우편번호", "수령인 주소", "수령인 상세 주소",
            "수령인 연락처");

        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(
            new Order(orderId, member, null, new ArrayList<>(), null, OrderStatus.TEMP, "", now,
                now)));

        given(memberUtil.getMember(any())).willReturn(member);

        CompleteOrderResponse completeOrderResponse = orderService.completeOrder(
            completeOrderRequest, request);

        assertThat(completeOrderResponse.getOrderNumber()).contains("ORDER-");
    }
}
