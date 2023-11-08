package ai.serverapi.order.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ai.serverapi.ControllerBaseTest;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.response.LoginResponse;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.order.dto.request.TempOrderDto;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class OrderControllerDocs extends ControllerBaseTest {


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

    @Test
    @DisplayName(PREFIX + " (POST)")
    void getProductList() throws Exception {
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest searchDto1 = new ProductRequest(1L, "검색 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1",
            "https://image2", "https://image3", "normal");
        ProductRequest searchDto2 = new ProductRequest(1L, "검색 제목2", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1",
            "https://image2", "https://image3", "normal");

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

        ResultActions resultActions = mockMvc.perform(
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
}
