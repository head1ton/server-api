package ai.serverapi.order.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.member.enums.Role;
import ai.serverapi.order.domain.Orders;
import ai.serverapi.order.domain.OrdersDetail;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.TempOrderResponse;
import ai.serverapi.order.enums.OrdersStatus;
import ai.serverapi.order.repository.OrdersDetailRepostiory;
import ai.serverapi.order.repository.OrdersRepository;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.enums.CategoryStatus;
import ai.serverapi.product.enums.Status;
import ai.serverapi.product.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith({MockitoExtension.class})
public class OrderServiceUnitTest {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private MemberUtil memberUtil;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private OrdersDetailRepostiory ordersDetailRepository;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품 id로 인해 실패")
    void postTempOrderFail1() {
        TempOrderDto tempOrderDto = new TempOrderDto(1L, 5);
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        tempOrderDtoList.add(tempOrderDto);

        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품 id");
    }

    @Test
    @DisplayName("유효하지 않은 상품이 존재해 실패")
    void postTempOrderFail2() {
        TempOrderDto tempOrderDto1 = new TempOrderDto(1L, 5);
        TempOrderDto tempOrderDto2 = new TempOrderDto(2L, 3);
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);

        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);
        LocalDateTime now = LocalDateTime.now();

        Member member = new Member(1L, "email@mail.com", "password", "nickname", "name",
            "19941030",
            Role.SELLER, null, null, now, now);
        given(memberUtil.getMember(any())).willReturn(member);
        List<Product> productList = new ArrayList<>();
        Seller seller = new Seller(1L, member, "company", "01012341234", "우편주소", "주소", "상세 주소",
            "mail@gmail.com");
        Category category = Category.of("카테고리", CategoryStatus.USE);

        Product product1 = new Product(1L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000,
            "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L,
            Status.HIDDEN, now, now);
        Product product2 = new Product(2L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 3000,
            "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L,
            Status.NORMAL, now, now);

        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);

        assertThatCode(() -> orderService.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품이 존재합니다.");
    }

    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {
        TempOrderDto tempOrderDto1 = new TempOrderDto(1L, 5);
        TempOrderDto tempOrderDto2 = new TempOrderDto(5L, 3);
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);

        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        JoinRequest joinRequest = new JoinRequest("email@gmal.com", "password", "tester",
            "nickname", "19941030");
        Member member = Member.of(joinRequest);
        Seller seller = Seller.of(member, "company", "tel", "zonecode", "address", "addressDetail",
            "mail@gmail.com");
        Category category = Category.of("카테고리", CategoryStatus.USE);
        LocalDateTime now = LocalDateTime.now();

        Product product1 = new Product(1L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000, "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L, Status.NORMAL, now, now);
        Product product2 = new Product(5L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000, "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L, Status.NORMAL, now, now);
        productList.add(product1);
        productList.add(product2);

        given(productRepository.findAllById(any())).willReturn(productList);

        Orders orders = new Orders(1L, member, OrdersStatus.TEMP, null, null, null, null, null,
            null, null, null, 0, null, now, now);
        given(ordersRepository.save(any())).willReturn(orders);
        given(ordersDetailRepository.save(any())).willReturn(new OrdersDetail());
        //when
        TempOrderResponse tempOrderResponse = orderService.postTempOrder(tempOrderRequest, request);
        //then
        int totalPrice = 0;
        totalPrice += product1.getPrice() * tempOrderDto1.getEa();
        totalPrice += product2.getPrice() * tempOrderDto2.getEa();
        Assertions.assertThat(tempOrderResponse.getTotalPrice()).isEqualTo(totalPrice);

    }
}
