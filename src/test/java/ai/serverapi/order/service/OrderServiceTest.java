package ai.serverapi.order.service;

import static ai.serverapi.Base.MEMBER_LOGIN;
import static ai.serverapi.Base.PRODUCT_ID_MASK;
import static ai.serverapi.Base.PRODUCT_ID_NORMAL;
import static ai.serverapi.Base.PRODUCT_OPTION_ID_MASK;
import static ai.serverapi.OrderBase.ORDER_FIRST_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.member.repository.MemberJpaRepository;
import ai.serverapi.member.repository.SellerJpaRepository;
import ai.serverapi.member.service.MemberAuthServiceImpl;
import ai.serverapi.order.controller.request.CompleteOrderRequest;
import ai.serverapi.order.controller.request.TempOrderDto;
import ai.serverapi.order.controller.request.TempOrderRequest;
import ai.serverapi.order.controller.response.CompleteOrderResponse;
import ai.serverapi.order.controller.response.PostTempOrderResponse;
import ai.serverapi.order.repository.DeliveryJpaRepository;
import ai.serverapi.order.repository.OrderItemJpaRepository;
import ai.serverapi.order.repository.OrderJpaRepository;
import ai.serverapi.product.repository.CategoryJpaRepository;
import ai.serverapi.product.repository.OptionJpaRepository;
import ai.serverapi.product.repository.ProductJpaRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@SpringBootTest
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql", "/sql/product.sql",
        "/sql/order.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional(readOnly = true)
@Execution(ExecutionMode.CONCURRENT)
class OrderServiceTest {

    @Autowired
    private MemberAuthServiceImpl memberAuthService;
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private SellerJpaRepository sellerJpaRepository;
    @Autowired
    private OptionJpaRepository optionJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private CategoryJpaRepository categoryJpaRepository;
    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;
    @Autowired
    private DeliveryJpaRepository deliveryJpaRepository;

    @AfterEach
    void cleanUp() {
        deliveryJpaRepository.deleteAll();
        orderItemJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        optionJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
        categoryJpaRepository.deleteAll();
        sellerJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("임시 주문 등록")
    void tempOrder() {
        TempOrderRequest tempOrderRequest = TempOrderRequest.builder()
                                                            .orderList(List.of(
                                                                TempOrderDto.builder()
                                                                            .ea(2)
                                                                            .productId(
                                                                                PRODUCT_ID_NORMAL)
                                                                            .build(),
                                                                TempOrderDto.builder()
                                                                            .ea(3)
                                                                            .productId(
                                                                                PRODUCT_ID_MASK)
                                                                            .optionId(
                                                                                PRODUCT_OPTION_ID_MASK)
                                                                            .build()
                                                            ))
                                                 .build();

        request.addHeader(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken());

        PostTempOrderResponse postTempOrderResponse = orderService.postTempOrder(tempOrderRequest,
            request);

        assertThat(postTempOrderResponse.getOrderId()).isGreaterThan(0L);
    }

    @Test
    @DisplayName("주문 완료")
    void completeOrder() {
        request.addHeader(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken());
        CompleteOrderRequest completeOrderRequest = CompleteOrderRequest.builder()
                                                                        .orderId(ORDER_FIRST_ID)
                                                                        .ownerAddress("주소")
                                                                        .ownerAddressDetail("상세 주소")
                                                                        .ownerName("주문자")
                                                                        .ownerTel("01012341234")
                                                                        .recipientAddress("수령인 주소")
                                                                        .recipientAddressDetail(
                                                                            "수령인 상세 주소")
                                                                        .recipientName("수령인")
                                                                        .recipientTel("01012341234")
                                                                        .build();

        CompleteOrderResponse completeOrderResponse = orderService.completeOrder(
            completeOrderRequest, request);

        assertThat(completeOrderResponse.getOrderNumber()).isNotNull();
    }

//    @Test
//    @DisplayName("관리자툴에서 주문 불러오기 성공")
//    @Transactional
//    void getOrderList() {
//
//        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());
//
//        Pageable pageable = Pageable.ofSize(10);
//        OrderResponse complete = orderService.getOrderListBySeller(pageable, "", "COMPLETE",
//            request);
//
//        System.out.println("complete = " + complete.getTotalElements());
//
//        assertThat(complete.getTotalElements()).isGreaterThan(1);
//    }
}
