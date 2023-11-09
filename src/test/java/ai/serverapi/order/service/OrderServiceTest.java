package ai.serverapi.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.BaseTest;
import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.response.LoginResponse;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.order.domain.Order;
import ai.serverapi.order.domain.OrderItem;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.PostTempOrderResponse;
import ai.serverapi.order.repository.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class OrderServiceTest extends BaseTest {

    @Autowired
    private MemberAuthService memberAuthService;
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("임시 주문 성공")
    @Transactional
    void tempOrder() {
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = new TempOrderDto(PRODUCT1.getId(), 3);
        TempOrderDto tempOrderDto2 = new TempOrderDto(PRODUCT2.getId(), 3);
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        //when
        PostTempOrderResponse postTempOrderResponse = orderService.postTempOrder(tempOrderRequest,
            request);

        //then
        Long orderId = postTempOrderResponse.getOrderId();
        Order order = orderRepository.findById(orderId).get();
        List<OrderItem> orderItemList = order.getOrderItemList();
        assertThat(orderItemList).isNotEmpty();
    }
}
