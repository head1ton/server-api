package ai.serverapi.order.service;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.order.repository.OrderItemRepository;
import ai.serverapi.order.repository.OrderRepository;
import ai.serverapi.product.repository.ProductRepository;
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
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품 id로 인해 실패")
    void postTempOrderFail1() {

    }

    @Test
    @DisplayName("유효하지 않은 상품이 존재해 실패")
    void postTempOrderFail2() {

    }

    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {

    }
}
