package ai.serverapi.order.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.order.dto.request.TempOrder;
import ai.serverapi.order.dto.request.TempOrderRequest;
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
    private OrdersDetailRepostiory ordersDetailRepostiory;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품 id로 인해 실패")
    void postTempOrderFail2() {
        TempOrder tempOrder = new TempOrder(1L, 5);
        List<TempOrder> tempOrderList = new ArrayList<>();
        tempOrderList.add(tempOrder);

        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderList);

        assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품 id");
    }

    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {
        TempOrder tempOrder1 = new TempOrder(1L, 5);
        TempOrder tempOrder2 = new TempOrder(5L, 3);
        List<TempOrder> tempOrderList = new ArrayList<>();
        tempOrderList.add(tempOrder1);
        tempOrderList.add(tempOrder2);

        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderList);

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

        orderService.postTempOrder(tempOrderRequest, request);

    }
}
