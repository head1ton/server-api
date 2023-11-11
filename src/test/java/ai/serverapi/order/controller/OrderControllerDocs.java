package ai.serverapi.order.controller;

import static ai.serverapi.Base.CATEGORY_ID_BEAUTY;
import static ai.serverapi.Base.MEMBER_EMAIL;
import static ai.serverapi.Base.SELLER_EMAIL;
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
import ai.serverapi.member.domain.Seller;
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
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.dto.request.OptionRequest;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.enums.OptionStatus;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.OptionRepository;
import ai.serverapi.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(CATEGORY_ID_BEAUTY).get();

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(null, "option1", 1000,
            OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);

        ProductRequest searchDto1 = new ProductRequest(CATEGORY_ID_BEAUTY, "검색 제목", "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1",
            "https://image2", "https://image3", "normal", 10, optionRequestList, "normal");
        ProductRequest searchDto2 = new ProductRequest(CATEGORY_ID_BEAUTY, "검색 제목2", "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1",
            "https://image2", "https://image3", "normal", 10, optionRequestList, "normal");

        Seller seller = sellerRepository.findByMember(member).get();
        Product saveProduct1 = productRepository.save(Product.of(seller, category, searchDto1));
        Product saveProduct2 = productRepository.save(Product.of(seller, category, searchDto2));

        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);

        List<TempOrderDto> orderList = new ArrayList<>();
        int orderEa1 = 3;
        int orderEa2 = 2;

        TempOrderDto order1 = new TempOrderDto(saveProduct1.getId(), orderEa1);
        TempOrderDto order2 = new TempOrderDto(saveProduct2.getId(), orderEa2);
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
                fieldWithPath("order_list[].ea").type(JsonFieldType.NUMBER).description("주문 개수")
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

        Member sellerMember = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(CATEGORY_ID_BEAUTY).get();

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(null, "option1", 1000,
            OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);

        ProductRequest searchDto1 = new ProductRequest(CATEGORY_ID_BEAUTY, "검색 제목", "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1",
            "https://image2", "https://image3", "normal", 10, optionRequestList, "normal");
        ProductRequest searchDto2 = new ProductRequest(CATEGORY_ID_BEAUTY, "검색 제목2", "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명", 15000, 10000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1",
            "https://image2", "https://image3", "normal", 10, optionRequestList, "normal");

        Seller seller = sellerRepository.findByMember(sellerMember).get();
        Product saveProduct1 = productRepository.save(Product.of(seller, category, searchDto1));
        Product saveProduct2 = productRepository.save(Product.of(seller, category, searchDto2));

        orderItemRepository.save(OrderItem.of(order, saveProduct1, 3));
        orderItemRepository.save(OrderItem.of(order, saveProduct2, 5));

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
                    JsonFieldType.STRING).description("상품 카테고리 수정일")
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

        Member sellerMember = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(CATEGORY_ID_BEAUTY).get();

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(null, "option1", 1000,
            OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);

        ProductRequest searchDto1 = new ProductRequest(CATEGORY_ID_BEAUTY, "검색 제목", "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1",
            "https://image2", "https://image3", "normal", 10, optionRequestList, "normal");
        ProductRequest searchDto2 = new ProductRequest(CATEGORY_ID_BEAUTY, "검색 제목2", "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명", 15000, 10000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1",
            "https://image2", "https://image3", "normal", 10, optionRequestList, "normal");

        Seller seller = sellerRepository.findByMember(sellerMember).get();
        Product saveProduct1 = productRepository.save(Product.of(seller, category, searchDto1));
        Product saveProduct2 = productRepository.save(Product.of(seller, category, searchDto2));

        orderItemRepository.save(OrderItem.of(order, saveProduct1, 3));
        orderItemRepository.save(OrderItem.of(order, saveProduct2, 5));

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
