package ai.serverapi.order.service;

import static ai.serverapi.Base.MEMBER_EMAIL;
import static ai.serverapi.Base.PRODUCT_ID_MASK;
import static ai.serverapi.Base.PRODUCT_ID_PEAR;
import static ai.serverapi.Base.PRODUCT_OPTION_ID_MASK;
import static ai.serverapi.Base.PRODUCT_OPTION_ID_PEAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.response.LoginResponse;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.order.domain.Order;
import ai.serverapi.order.domain.OrderItem;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.PostTempOrderResponse;
import ai.serverapi.order.repository.OrderRepository;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.OptionRepository;
import ai.serverapi.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql",
        "/sql/order.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional(readOnly = true)
@Execution(ExecutionMode.CONCURRENT)
class OrderServiceTest {

    @Autowired
    private MemberAuthService memberAuthService;
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        optionRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        sellerRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("임시 주문 성공")
    @Transactional
    void tempOrder() {
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_MASK)
                                                 .optionId(PRODUCT_OPTION_ID_MASK)
                                                 .ea(3)
                                                 .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
                                                 .productId(PRODUCT_ID_PEAR)
                                                 .optionId(PRODUCT_OPTION_ID_PEAR)
                                                 .ea(3)
                                                 .build();
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
