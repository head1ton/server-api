package ai.serverapi.order.service;

import ai.serverapi.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrderServiceTest extends BaseTest {

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {

    }

}