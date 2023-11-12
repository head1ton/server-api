package ai.serverapi.order.controller;

import static ai.serverapi.Base.MEMBER_EMAIL;
import static ai.serverapi.Base.PRODUCT_ID_MASK;
import static ai.serverapi.Base.PRODUCT_ID_NORMAL;
import static ai.serverapi.Base.PRODUCT_ID_PEAR;
import static ai.serverapi.Base.PRODUCT_OPTION_ID_MASK;
import static ai.serverapi.Base.PRODUCT_OPTION_ID_PEAR;
import static ai.serverapi.Base.objectMapper;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ai.serverapi.RestdocsBaseTest;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.response.LoginResponse;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.order.domain.Order;
import ai.serverapi.order.domain.OrderItem;
import ai.serverapi.order.dto.request.CompleteOrderRequest;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.repository.DeliveryRepository;
import ai.serverapi.order.repository.OrderItemRepository;
import ai.serverapi.order.repository.OrderRepository;
import ai.serverapi.product.domain.Option;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.OptionRepository;
import ai.serverapi.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql",
        "/sql/order.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Execution(ExecutionMode.CONCURRENT)
class OrderControllerDocs extends RestdocsBaseTest {


    private final static String PREFIX = "/api/order";
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;

    @AfterEach
    void cleanUp() {
        deliveryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        optionRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        sellerRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName(PREFIX + " (POST)")
    void postTempOrder() throws Exception {

        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);

        List<TempOrderDto> orderList = new ArrayList<>();
        int orderEa1 = 3;
        int orderEa2 = 2;

        TempOrderDto order1 = TempOrderDto.builder()
                                          .productId(PRODUCT_ID_MASK)
                                          .optionId(PRODUCT_OPTION_ID_MASK)
                                          .ea(orderEa1)
                                          .build();
        TempOrderDto order2 = TempOrderDto.builder()
                                          .productId(PRODUCT_ID_PEAR)
                                          .optionId(PRODUCT_OPTION_ID_PEAR)
                                          .ea(orderEa2)
                                          .build();
        orderList.add(order1);
        orderList.add(order2);

        TempOrderRequest tempOrderRequest = new TempOrderRequest(orderList);

        ResultActions resultActions = mock.perform(
            post(PREFIX)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + login.accessToken())
                .content(objectMapper.writeValueAsString(tempOrderRequest))
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (MEMBER 권한 이상)")
            ),
            requestFields(
                fieldWithPath("order_list").type(JsonFieldType.ARRAY).description("주문 리스트"),
                fieldWithPath("order_list[].product_id").type(JsonFieldType.NUMBER)
                                                        .description("상품 id"),
                fieldWithPath("order_list[].ea").type(JsonFieldType.NUMBER).description("주문 개수"),
                fieldWithPath("order_list[].option_id").type(JsonFieldType.NUMBER)
                                                       .description("상품 옵션 id").optional()
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.order_id").type(JsonFieldType.NUMBER).description("주문 id")

            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/temp/{order_id} (GET)")
    void getTempOrder() throws Exception {
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();

        Order order = orderRepository.save(Order.of(member, "테스트 상품"));

        Product normalProduct = productRepository.findById(PRODUCT_ID_NORMAL).get();
        Product optionProduct = productRepository.findById(PRODUCT_ID_PEAR).get();

        Option option1 = optionRepository.findById(PRODUCT_ID_MASK).get();

        orderItemRepository.save(OrderItem.of(order, normalProduct, null, 3));
        orderItemRepository.save(OrderItem.of(order, optionProduct, option1, 5));

        ResultActions resultActions = mock.perform(
            get(PREFIX + "/temp/{order_id}", order.getId())
                .header(AUTHORIZATION, "Bearer " + login.accessToken())
        );
        resultActions.andExpect(status().is2xxSuccessful());
        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (MEMBER 권한 이상)")
            ),
            pathParameters(
                parameterWithName("order_id").description("주문 id")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.order_id").type(JsonFieldType.NUMBER).description("주문 id"),
                fieldWithPath("data.order_item_list").type(JsonFieldType.ARRAY)
                                                     .description("주문 상품 리스트"),
                fieldWithPath("data.order_item_list[].product_id").type(JsonFieldType.NUMBER)
                                                                  .description("상품 id"),
                fieldWithPath("data.order_item_list[].ea").type(JsonFieldType.NUMBER)
                                                          .description("상품 주문 개수"),
                fieldWithPath("data.order_item_list[].main_title").type(JsonFieldType.STRING)
                                                                  .description("주문 상품명"),
                fieldWithPath("data.order_item_list[].main_explanation").type(JsonFieldType.STRING)
                                                                        .description("주문 상품 메인 설명"),
                fieldWithPath("data.order_item_list[].product_main_explanation").type(
                    JsonFieldType.STRING).description("주문 상품 상세 설명"),
                fieldWithPath("data.order_item_list[].product_sub_explanation").type(
                    JsonFieldType.STRING).description("주문 상품 상세 서브 설명"),
                fieldWithPath("data.order_item_list[].origin_price").type(JsonFieldType.NUMBER)
                                                                    .description("주문 상품 원가"),
                fieldWithPath("data.order_item_list[].price").type(JsonFieldType.NUMBER)
                                                             .description("주문 상품 판매가"),
                fieldWithPath("data.order_item_list[].purchase_inquiry").type(JsonFieldType.STRING)
                                                                        .description(
                                                                            "주문 상품 취급 주의 사항"),
                fieldWithPath("data.order_item_list[].origin").type(JsonFieldType.STRING)
                                                              .description("원산지"),
                fieldWithPath("data.order_item_list[].producer").type(JsonFieldType.STRING)
                                                                .description("공급자"),
                fieldWithPath("data.order_item_list[].main_image").type(JsonFieldType.STRING)
                                                                  .description("메인 이미지"),
                fieldWithPath("data.order_item_list[].image1").type(JsonFieldType.STRING)
                                                              .description("이미지1"),
                fieldWithPath("data.order_item_list[].image2").type(JsonFieldType.STRING)
                                                              .description("이미지2"),
                fieldWithPath("data.order_item_list[].image3").type(JsonFieldType.STRING)
                                                              .description("이미지3"),
                fieldWithPath("data.order_item_list[].view_cnt").type(JsonFieldType.NUMBER)
                                                                .description("조회수"),
                fieldWithPath("data.order_item_list[].status").type(JsonFieldType.STRING)
                                                              .description("상품 상태"),
                fieldWithPath("data.order_item_list[].created_at").type(JsonFieldType.STRING)
                                                                  .description("상품 생성일"),
                fieldWithPath("data.order_item_list[].modified_at").type(JsonFieldType.STRING)
                                                                   .description("상품 수정일"),
                fieldWithPath("data.order_item_list[].seller").type(JsonFieldType.OBJECT)
                                                              .description("상품 판매자"),
                fieldWithPath("data.order_item_list[].seller.seller_id").type(JsonFieldType.NUMBER)
                                                                        .description("상품 판매자 id"),
                fieldWithPath("data.order_item_list[].seller.email").type(JsonFieldType.STRING)
                                                                    .description("상품 판매자 email"),
                fieldWithPath("data.order_item_list[].seller.company").type(JsonFieldType.STRING)
                                                                      .description("상품 판매자 회사명"),
                fieldWithPath("data.order_item_list[].seller.zonecode").type(JsonFieldType.STRING)
                                                                       .description("상품 판매자 우편 주소"),
                fieldWithPath("data.order_item_list[].seller.address").type(JsonFieldType.STRING)
                                                                      .description("상품 판매자 주소"),
                fieldWithPath("data.order_item_list[].seller.address_detail").type(
                    JsonFieldType.STRING).description("상품 판매자 상세 주소"),
                fieldWithPath("data.order_item_list[].seller.tel").type(JsonFieldType.STRING)
                                                                  .description("상품 판매자 연락처"),
                fieldWithPath("data.order_item_list[].category").type(JsonFieldType.OBJECT)
                                                                .description("상품 카테고리"),
                fieldWithPath("data.order_item_list[].category.category_id").type(
                    JsonFieldType.NUMBER).description("상품 카테고리 id"),
                fieldWithPath("data.order_item_list[].category.name").type(JsonFieldType.STRING)
                                                                     .description("상품 카테고리명"),
                fieldWithPath("data.order_item_list[].category.created_at").type(
                    JsonFieldType.STRING).description("상품 카테고리 생성일"),
                fieldWithPath("data.order_item_list[].category.modified_at").type(
                    JsonFieldType.STRING).description("상품 카테고리 수정일"),
                fieldWithPath("data.order_item_list[].option").type(JsonFieldType.OBJECT)
                                                              .description("상품 옵션").optional(),
                fieldWithPath("data.order_item_list[].option.option_id").type(JsonFieldType.NUMBER)
                                                                        .description("상품 옵션 id")
                                                                        .optional(),
                fieldWithPath("data.order_item_list[].option.name").type(JsonFieldType.STRING)
                                                                   .description("상품 옵션명")
                                                                   .optional(),
                fieldWithPath("data.order_item_list[].option.extra_price").type(
                    JsonFieldType.NUMBER).description("상품 옵션 추가 금액").optional(),
                fieldWithPath("data.order_item_list[].option.ea").type(JsonFieldType.NUMBER)
                                                                 .description("상품 옵션 재고")
                                                                 .optional(),
                fieldWithPath("data.order_item_list[].option.created_at").type(JsonFieldType.STRING)
                                                                         .description("상품 옵션 생성일")
                                                                         .optional(),
                fieldWithPath("data.order_item_list[].option.modified_at").type(
                    JsonFieldType.STRING).description("상품 옵션 수정일").optional()
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/complete (PATCH)")
    void complete() throws Exception {
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();

        Order order = orderRepository.save(Order.of(member, "테스트 상품"));
        Long orderId = order.getId();

        Product product1 = productRepository.findById(PRODUCT_ID_MASK).get();
        Product product2 = productRepository.findById(PRODUCT_ID_NORMAL).get();

        Option option1 = optionRepository.findById(PRODUCT_OPTION_ID_MASK).get();

        orderItemRepository.save(OrderItem.of(order, product1, option1, 3));
        orderItemRepository.save(OrderItem.of(order, product2, null, 5));

        CompleteOrderRequest completeOrderRequest = new CompleteOrderRequest(orderId, "주문자",
            "주문자 우편번호", "주문자 주소", "주문자 상세 주소", "주문자 연락처", "수령인", "수령인 우편번호", "수령인 주소", "수령인 상세 주소",
            "수령인 연락처");

        ResultActions resultActions = mock.perform(
            patch(PREFIX + "/complete")
                .header(AUTHORIZATION, "Bearer " + login.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(completeOrderRequest))
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (MEMBER 권한 이상)")
            ),
            requestFields(
                fieldWithPath("order_id").description("주문 id"),
                fieldWithPath("owner_name").description("주문자"),
                fieldWithPath("owner_zonecode").description("주문자 우편번호"),
                fieldWithPath("owner_address").description("주문자 주소"),
                fieldWithPath("owner_address_detail").description("주문자 상세 주소"),
                fieldWithPath("owner_tel").description("주문자 연락처"),
                fieldWithPath("recipient_name").description("수령인"),
                fieldWithPath("recipient_zonecode").description("수령인 우편번호"),
                fieldWithPath("recipient_address").description("수령인 주소"),
                fieldWithPath("recipient_address_detail").description("수령인 상세 주소"),
                fieldWithPath("recipient_tel").description("수령인 연락처")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.order_id").type(JsonFieldType.NUMBER).description("주문 id"),
                fieldWithPath("data.order_number").type(JsonFieldType.STRING).description("주문 번호")
            )
        ));
    }
}
