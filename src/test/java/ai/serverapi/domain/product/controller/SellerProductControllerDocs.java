package ai.serverapi.domain.product.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ai.serverapi.BaseTest;
import ai.serverapi.domain.member.dto.LoginDto;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.service.MemberAuthService;
import ai.serverapi.domain.member.vo.LoginVo;
import ai.serverapi.domain.product.dto.ProductDto;
import ai.serverapi.domain.product.dto.PutProductDto;
import ai.serverapi.domain.product.entity.Category;
import ai.serverapi.domain.product.entity.Product;
import ai.serverapi.domain.product.repository.CategoryRepository;
import ai.serverapi.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SellerProductControllerDocs extends BaseTest {

    private final static String PREFIX = "/api/seller/product";
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName(PREFIX + "(GET)")
    void getProductList() throws Exception {
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);

        Member seller = memberRepository.findByEmail(SELLER_EMAIL).get();
        Member seller2 = memberRepository.findByEmail(SELLER2_EMAIL).get();

        ProductDto productDto = new ProductDto(1L, "다른 유저 상품", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal");
        ProductDto searchDto = new ProductDto(1L, "셀러 유저 상품", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal");

        Category category = categoryRepository.findById(1L).get();

        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 25; i++) {
            productRepository.save(Product.of(seller2, category, productDto));
        }

        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, searchDto));
        }

        ResultActions perform = mockMvc.perform(
            get(PREFIX)
                .param("search", "").param("page", "0").param("size", "5").param("status", "normal")
                .header(AUTHORIZATION, "Bearer " + login.getAccessToken())
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (SELLER 권한 유저)")
            ),
            queryParameters(
                parameterWithName("page").description("paging 시작 페이지 번호").optional(),
                parameterWithName("size").description("paging 시작 페이지 기준 개수 크기").optional(),
                parameterWithName("search").description("검색어").optional(),
                parameterWithName("status").description(
                    "상품 상태값 (일반: normal, 숨김: hidden, 삭제: delete / 대소문자 구분 없음)").optional()
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.total_page").type(JsonFieldType.NUMBER).description("페이지 총 개수"),
                fieldWithPath("data.total_elements").type(JsonFieldType.NUMBER)
                                                    .description("총 데이터수"),
                fieldWithPath("data.number_of_elements").type(JsonFieldType.NUMBER)
                                                        .description("페이지 총 데이터수"),
                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                                          .description("페이지 마지막 데이터 여부"),
                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN)
                                           .description("페이지 데이터 존재 여부"),
                fieldWithPath("data.list[]").type(JsonFieldType.ARRAY).description("등록 상품 list"),
                fieldWithPath("data.list[].id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
                fieldWithPath("data.list[].main_title").type(JsonFieldType.STRING)
                                                       .description("메인 타이틀"),
                fieldWithPath("data.list[].main_explanation").type(JsonFieldType.STRING)
                                                             .description("메인 설명"),
                fieldWithPath("data.list[].product_main_explanation").type(JsonFieldType.STRING)
                                                                     .description("상품 메인 설명"),
                fieldWithPath("data.list[].product_sub_explanation").type(JsonFieldType.STRING)
                                                                    .description("상품 서브 설명"),
                fieldWithPath("data.list[].origin_price").type(JsonFieldType.NUMBER)
                                                         .description("상품 원가"),
                fieldWithPath("data.list[].price").type(JsonFieldType.NUMBER)
                                                  .description("상품 실제 판매 가격"),
                fieldWithPath("data.list[].purchase_inquiry").type(JsonFieldType.STRING)
                                                             .description("취급 방법"),
                fieldWithPath("data.list[].origin").type(JsonFieldType.STRING).description("원산지"),
                fieldWithPath("data.list[].producer").type(JsonFieldType.STRING).description("공급자"),
                fieldWithPath("data.list[].main_image").type(JsonFieldType.STRING)
                                                       .description("메인 이미지 url"),
                fieldWithPath("data.list[].image1").type(JsonFieldType.STRING).description("이미지1"),
                fieldWithPath("data.list[].image2").type(JsonFieldType.STRING).description("이미지2"),
                fieldWithPath("data.list[].image3").type(JsonFieldType.STRING).description("이미지3"),
                fieldWithPath("data.list[].view_cnt").type(JsonFieldType.NUMBER).description("조회수"),
                fieldWithPath("data.list[].status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.list[].created_at").type(JsonFieldType.STRING)
                                                       .description("생성일"),
                fieldWithPath("data.list[].modified_at").type(JsonFieldType.STRING)
                                                        .description("수정일"),
                fieldWithPath("data.list[].seller.member_id").type(JsonFieldType.NUMBER)
                                                             .description("판매자 id"),
                fieldWithPath("data.list[].seller.email").type(JsonFieldType.STRING)
                                                         .description("판매자 email"),
                fieldWithPath("data.list[].seller.nickname").type(JsonFieldType.STRING)
                                                            .description("판매자 닉네임"),
                fieldWithPath("data.list[].seller.name").type(JsonFieldType.STRING)
                                                        .description("판매자 이름"),
                fieldWithPath("data.list[].category.category_id").type(JsonFieldType.NUMBER)
                                                                 .description("카테고리 id"),
                fieldWithPath("data.list[].category.name").type(JsonFieldType.STRING)
                                                          .description("카테고리 명"),
                fieldWithPath("data.list[].category.created_at").type(JsonFieldType.STRING)
                                                                .description("카테고리 생성일"),
                fieldWithPath("data.list[].category.modified_at").type(JsonFieldType.STRING)
                                                                 .description("카테고리 수정일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "(POST)")
    void postProduct() throws Exception {
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);
        ProductDto productDto = new ProductDto(1L, "메인 타이틀", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            9000, "취급 방법", "원산지", "공급자", "https://main_image", "https://image1", "https://image2",
            "https://image3", "normal");

        ResultActions perform = mockMvc.perform(
            post(PREFIX)
                .header(AUTHORIZATION, "Bearer " + login.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(productDto))
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (SELLER 권한 유저)")
            ),
            requestFields(
                fieldWithPath("category_id").type(JsonFieldType.NUMBER)
                                            .description("카테고리 id (1: 화장품, 2: 건강식품, 3: 생활용품)"),
                fieldWithPath("main_title").type(JsonFieldType.STRING).description("제목"),
                fieldWithPath("main_explanation").type(JsonFieldType.STRING).description("메인 설명"),
                fieldWithPath("product_main_explanation").type(JsonFieldType.STRING)
                                                         .description("상품 메인 설명"),
                fieldWithPath("product_sub_explanation").type(JsonFieldType.STRING)
                                                        .description("상품 보조 설명"),
                fieldWithPath("origin_price").type(JsonFieldType.NUMBER).description("원가"),
                fieldWithPath("price").type(JsonFieldType.NUMBER).description("실제 판매가"),
                fieldWithPath("purchase_inquiry").type(JsonFieldType.STRING).description("취급방법"),
                fieldWithPath("origin").type(JsonFieldType.STRING).description("원산지"),
                fieldWithPath("producer").type(JsonFieldType.STRING).description("공급자"),
                fieldWithPath("main_image").type(JsonFieldType.STRING).description("메인 이미지"),
                fieldWithPath("image1").type(JsonFieldType.STRING).description("이미지1").optional(),
                fieldWithPath("image2").type(JsonFieldType.STRING).description("이미지2").optional(),
                fieldWithPath("image3").type(JsonFieldType.STRING).description("이미지3").optional(),
                fieldWithPath("status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)").optional()
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
                fieldWithPath("data.main_title").type(JsonFieldType.STRING).description("메인 타이틀"),
                fieldWithPath("data.main_explanation").type(JsonFieldType.STRING)
                                                      .description("메인 설명"),
                fieldWithPath("data.product_main_explanation").type(JsonFieldType.STRING)
                                                              .description("상품 메인 설명"),
                fieldWithPath("data.product_sub_explanation").type(JsonFieldType.STRING)
                                                             .description("상품 서브 설명"),
                fieldWithPath("data.origin_price").type(JsonFieldType.NUMBER).description("상품 원가"),
                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 실제 판매 가격"),
                fieldWithPath("data.purchase_inquiry").type(JsonFieldType.STRING)
                                                      .description("취급 방법"),
                fieldWithPath("data.origin").type(JsonFieldType.STRING).description("원산지"),
                fieldWithPath("data.producer").type(JsonFieldType.STRING).description("공급자"),
                fieldWithPath("data.main_image").type(JsonFieldType.STRING)
                                                .description("메인 이미지 url"),
                fieldWithPath("data.image1").type(JsonFieldType.STRING).description("이미지1"),
                fieldWithPath("data.image2").type(JsonFieldType.STRING).description("이미지2"),
                fieldWithPath("data.image3").type(JsonFieldType.STRING).description("이미지3"),
                fieldWithPath("data.view_cnt").type(JsonFieldType.NUMBER).description("조회수"),
                fieldWithPath("data.status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING)
                                                .description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING)
                                                 .description("수정일"),
                fieldWithPath("data.category.category_id").type(JsonFieldType.NUMBER)
                                                          .description("카테고리 id"),
                fieldWithPath("data.category.name").type(JsonFieldType.STRING)
                                                   .description("카테고리 명"),
                fieldWithPath("data.category.created_at").type(JsonFieldType.STRING)
                                                         .description("카테고리 생성일"),
                fieldWithPath("data.category.modified_at").type(JsonFieldType.STRING)
                                                          .description("카테고리 수정일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "(PUT)")
    void putProduct() throws Exception {
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(1L).get();

        ProductDto productDto = new ProductDto(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");
        Product originalProduct = productRepository.save(Product.of(member, category, productDto));
        Long productId = originalProduct.getId();
        PutProductDto putProductDto = new PutProductDto(productId, 2L, "수정된 제목", "수정된 설명",
            "상품 메인 설명",
            "상품 서브 설명", 12000, 10000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1",
            "https://image2", "https://image3", "normal");

        ResultActions perform = mockMvc.perform(
            put(PREFIX)
                .header(AUTHORIZATION, "Bearer " + login.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(putProductDto))
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (SELLER 권한 유저)")
            ),
            requestFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("상품 id"),
                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("카테고리 id"),
                fieldWithPath("main_title").type(JsonFieldType.STRING).description("제목"),
                fieldWithPath("main_explanation").type(JsonFieldType.STRING).description("메인 설명"),
                fieldWithPath("product_main_explanation").type(JsonFieldType.STRING)
                                                         .description("상품 메인 설명"),
                fieldWithPath("product_sub_explanation").type(JsonFieldType.STRING)
                                                        .description("상품 보조 설명"),
                fieldWithPath("origin_price").type(JsonFieldType.NUMBER).description("원가"),
                fieldWithPath("price").type(JsonFieldType.NUMBER).description("실제 판매가"),
                fieldWithPath("purchase_inquiry").type(JsonFieldType.STRING).description("취급방법"),
                fieldWithPath("origin").type(JsonFieldType.STRING).description("원산지"),
                fieldWithPath("producer").type(JsonFieldType.STRING).description("공급자"),
                fieldWithPath("main_image").type(JsonFieldType.STRING).description("메인 이미지"),
                fieldWithPath("image1").type(JsonFieldType.STRING).description("이미지1").optional(),
                fieldWithPath("image2").type(JsonFieldType.STRING).description("이미지2").optional(),
                fieldWithPath("image3").type(JsonFieldType.STRING).description("이미지3").optional(),
                fieldWithPath("status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반: normal, 숨김: hidden, 삭제: delete / 대소문자 구분 없음")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
                fieldWithPath("data.main_title").type(JsonFieldType.STRING).description("메인 타이틀"),
                fieldWithPath("data.main_explanation").type(JsonFieldType.STRING)
                                                      .description("메인 설명"),
                fieldWithPath("data.product_main_explanation").type(JsonFieldType.STRING)
                                                              .description("상품 메인 설명"),
                fieldWithPath("data.product_sub_explanation").type(JsonFieldType.STRING)
                                                             .description("상품 서브 설명"),
                fieldWithPath("data.origin_price").type(JsonFieldType.NUMBER).description("상품 원가"),
                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 실제 판매 가격"),
                fieldWithPath("data.purchase_inquiry").type(JsonFieldType.STRING)
                                                      .description("취급 방법"),
                fieldWithPath("data.origin").type(JsonFieldType.STRING).description("원산지"),
                fieldWithPath("data.producer").type(JsonFieldType.STRING).description("공급자"),
                fieldWithPath("data.main_image").type(JsonFieldType.STRING)
                                                .description("메인 이미지 url"),
                fieldWithPath("data.image1").type(JsonFieldType.STRING).description("이미지1"),
                fieldWithPath("data.image2").type(JsonFieldType.STRING).description("이미지2"),
                fieldWithPath("data.image3").type(JsonFieldType.STRING).description("이미지3"),
                fieldWithPath("data.view_cnt").type(JsonFieldType.NUMBER).description("조회수"),
                fieldWithPath("data.status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING)
                                                .description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING)
                                                 .description("수정일"),
                fieldWithPath("data.category.category_id").type(JsonFieldType.NUMBER)
                                                          .description("카테고리 id"),
                fieldWithPath("data.category.name").type(JsonFieldType.STRING)
                                                   .description("카테고리 명"),
                fieldWithPath("data.category.created_at").type(JsonFieldType.STRING)
                                                         .description("카테고리 생성일"),
                fieldWithPath("data.category.modified_at").type(JsonFieldType.STRING)
                                                          .description("카테고리 수정일")
            )
        ));
    }

}
