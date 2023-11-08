package ai.serverapi.product.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ai.serverapi.ControllerBaseTest;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.response.LoginResponse;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.dto.request.AddViewCntRequest;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProductControllerDocs extends ControllerBaseTest {

    private static final String PREFIX = "/api/product";

    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SellerRepository sellerRepository;

    @Test
    @DisplayName(PREFIX)
    void getProductList() throws Exception {

        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "htts://image2",
            "https://image3", "normal");

        ProductRequest searchDto = new ProductRequest(1L, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "htts://image2",
            "https://image3", "normal");

        Seller seller = sellerRepository.findByMember(member).get();

        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 25; i++) {
            productRepository.save(Product.of(seller, category, productRequest));
        }

        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, searchDto));
        }

        ResultActions perform = mockMvc.perform(
            get(PREFIX)
                .param("search", "검색")
                .param("page", "0")
                .param("size", "5")
                .param("status", "normal")
                .param("category_id", "0")
                .param("seller_id", "0")
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            queryParameters(
                parameterWithName("page").description("paging 시작 페이지 번호").optional(),
                parameterWithName("size").description("paging 시작 페이지 기준 개수 크기").optional(),
                parameterWithName("search").description("검색어").optional(),
                parameterWithName("status").description(
                    "상품 상태값 (일반: normal, 숨김: hidden, 삭제: delete / 대소문자 구분 없음)").optional(),
                parameterWithName("category_id").description(
                    "카테고리 검색 id (0: 전체, 1: 화장품, 2: 건강식품, 3: 생활용품)").optional(),
                parameterWithName("seller_id").description(
                    "판매자 id (seller_id로 검색시 판매자가 등록한 상품만 반환됨").optional()
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
                                                       .description("등록일"),
                fieldWithPath("data.list[].modified_at").type(JsonFieldType.STRING)
                                                        .description("수정일"),
                fieldWithPath("data.list[].seller.seller_id").type(JsonFieldType.NUMBER)
                                                             .description("판매자 id"),
                fieldWithPath("data.list[].seller.email").type(JsonFieldType.STRING)
                                                         .description("판매자 email"),
                fieldWithPath("data.list[].seller.company").type(JsonFieldType.STRING)
                                                           .description("판매자 회사"),
                fieldWithPath("data.list[].seller.tel").type(JsonFieldType.STRING)
                                                       .description("판매자 연락처"),
                fieldWithPath("data.list[].seller.zonecode").type(JsonFieldType.STRING)
                                                            .description("판매자 우편번호"),
                fieldWithPath("data.list[].seller.address").type(JsonFieldType.STRING)
                                                           .description("판매자 주소"),
                fieldWithPath("data.list[].seller.address_detail").type(JsonFieldType.STRING)
                                                                  .description("판매자 상세 주소"),
                fieldWithPath("data.list[].category.category_id").type(JsonFieldType.NUMBER)
                                                                 .description("카테고리 id"),
                fieldWithPath("data.list[].category.name").type(JsonFieldType.STRING)
                                                          .description("카테고리명"),
                fieldWithPath("data.list[].category.created_at").type(JsonFieldType.STRING)
                                                                .description("카테고리 생성일"),
                fieldWithPath("data.list[].category.modified_at").type(JsonFieldType.STRING)
                                                                 .description("카테고리 수정일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/{id}")
    void getProduct() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse login = memberAuthService.login(loginRequest);

        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal");

        Seller seller = sellerRepository.findByMember(member).get();
        Product product = productRepository.save(Product.of(seller, category, productRequest));

        ResultActions perform = mockMvc.perform(get(PREFIX + "/{id}", product.getId()));

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            pathParameters(
                parameterWithName("id").description("상품 id")
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
                fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("등록일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일"),
                fieldWithPath("data.seller.seller_id").type(JsonFieldType.NUMBER)
                                                      .description("판매자 id"),
                fieldWithPath("data.seller.email").type(JsonFieldType.STRING)
                                                  .description("판매자 email"),
                fieldWithPath("data.seller.company").type(JsonFieldType.STRING)
                                                    .description("판매자 회사"),
                fieldWithPath("data.seller.tel").type(JsonFieldType.STRING).description("판매자 연락처"),
                fieldWithPath("data.seller.zonecode").type(JsonFieldType.STRING)
                                                     .description("판매자 우편번호"),
                fieldWithPath("data.seller.address").type(JsonFieldType.STRING)
                                                    .description("판매자 주소"),
                fieldWithPath("data.seller.address_detail").type(JsonFieldType.STRING)
                                                           .description("판매자 상세 주소"),
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
    @DisplayName(PREFIX + "/category")
    void getCategoryList() throws Exception {
        ResultActions perform = mockMvc.perform(
            get(PREFIX + "/category")
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.list[].category_id").type(JsonFieldType.NUMBER)
                                                        .description("카테고리 id"),
                fieldWithPath("data.list[].name").type(JsonFieldType.STRING).description("카테고리 명"),
                fieldWithPath("data.list[].created_at").type(JsonFieldType.STRING)
                                                       .description("카테고리 생성일"),
                fieldWithPath("data.list[].modified_at").type(JsonFieldType.STRING)
                                                        .description("카테고리 수정일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/cnt")
    void addViewCnt() throws Exception {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Category category = categoryRepository.findById(1L).get();
        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "Https://mainImage", null, null, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();

        Product product = productRepository.save(Product.of(seller, category, productRequest));

        ResultActions resultActions = mockMvc.perform(
            patch(PREFIX + "/cnt")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new AddViewCntRequest(product.getId())))
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestFields(
                fieldWithPath("product_id").type(JsonFieldType.NUMBER).description("상품 id")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.message").type(JsonFieldType.STRING)
                                             .description("조회수 등록 여부 메세지")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/basket")
    void getProductBasket() throws Exception {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage",
            "https://image.s3.com", "https://image.s3.com", "https://image.s3.com", "normal");

        ProductRequest productRequest2 = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage",
            "https://image.s3.com", "https://image.s3.com", "https://image.s3.com", "normal");

        ProductRequest productRequest3 = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage",
            "https://image.s3.com", "https://image.s3.com", "https://image.s3.com", "normal");

        Seller seller = sellerRepository.findByMember(member).get();
        Product product1 = productRepository.save(Product.of(seller, category, productRequest));
        Product product2 = productRepository.save(Product.of(seller, category, productRequest2));
        Product product3 = productRepository.save(Product.of(seller, category, productRequest3));

        ResultActions perform = mockMvc.perform(
                                           get(PREFIX + "/basket").param("product_id", product3.getId().toString())
                                                                  .param("product_id", product1.getId().toString())
                                                                  .param("product_id", product2.getId().toString()))
                                       .andDo(print());

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            queryParameters(parameterWithName("product_id").description("상품 id").optional()),
            responseFields(fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.basket_list[]").type(JsonFieldType.ARRAY)
                                                   .description("등록 상품 list"),
                fieldWithPath("data.basket_list[].id").type(JsonFieldType.NUMBER)
                                                      .description("등록 상품 id"),
                fieldWithPath("data.basket_list[].main_title").type(JsonFieldType.STRING)
                                                              .description("메인 타이틀"),
                fieldWithPath("data.basket_list[].main_explanation").type(JsonFieldType.STRING)
                                                                    .description("메인 설명"),
                fieldWithPath("data.basket_list[].product_main_explanation").type(
                    JsonFieldType.STRING).description("상품 메인 설명"),
                fieldWithPath("data.basket_list[].product_sub_explanation").type(
                    JsonFieldType.STRING).description("상품 서브 설명"),
                fieldWithPath("data.basket_list[].origin_price").type(JsonFieldType.NUMBER)
                                                                .description("상품 원가"),
                fieldWithPath("data.basket_list[].price").type(JsonFieldType.NUMBER)
                                                         .description("상품 실제 판매 가격"),
                fieldWithPath("data.basket_list[].purchase_inquiry").type(JsonFieldType.STRING)
                                                                    .description("취급 방법"),
                fieldWithPath("data.basket_list[].origin").type(JsonFieldType.STRING)
                                                          .description("원산지"),
                fieldWithPath("data.basket_list[].producer").type(JsonFieldType.STRING)
                                                            .description("공급자"),
                fieldWithPath("data.basket_list[].main_image").type(JsonFieldType.STRING)
                                                              .description("메인 이미지 url"),
                fieldWithPath("data.basket_list[].image1").type(JsonFieldType.STRING)
                                                          .description("이미지1"),
                fieldWithPath("data.basket_list[].image2").type(JsonFieldType.STRING)
                                                          .description("이미지2"),
                fieldWithPath("data.basket_list[].image3").type(JsonFieldType.STRING)
                                                          .description("이미지3"),
                fieldWithPath("data.basket_list[].view_cnt").type(JsonFieldType.NUMBER)
                                                            .description("조회수"),
                fieldWithPath("data.basket_list[].status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.basket_list[].created_at").type(JsonFieldType.STRING)
                                                              .description("등록일"),
                fieldWithPath("data.basket_list[].modified_at").type(JsonFieldType.STRING)
                                                               .description("수정일"),
                fieldWithPath("data.basket_list[].seller.seller_id").type(JsonFieldType.NUMBER)
                                                                    .description("판매자 id"),
                fieldWithPath("data.basket_list[].seller.email").type(JsonFieldType.STRING)
                                                                .description("판매자 email"),
                fieldWithPath("data.basket_list[].seller.company").type(JsonFieldType.STRING)
                                                                  .description("판매자 회사명"),
                fieldWithPath("data.basket_list[].seller.tel").type(JsonFieldType.STRING)
                                                              .description("판매자 연락처"),
                fieldWithPath("data.basket_list[].seller.zonecode").type(JsonFieldType.STRING)
                                                                   .description("판매자 우편 주소"),
                fieldWithPath("data.basket_list[].seller.address").type(JsonFieldType.STRING)
                                                                  .description("판매자 주소"),
                fieldWithPath("data.basket_list[].seller.address_detail").type(JsonFieldType.STRING)
                                                                         .description("판매자 상세 주소"),
                fieldWithPath("data.basket_list[].category.category_id").type(JsonFieldType.NUMBER)
                                                                        .description("카테고리 id"),
                fieldWithPath("data.basket_list[].category.name").type(JsonFieldType.STRING)
                                                                 .description("카테고리 명"),
                fieldWithPath("data.basket_list[].category.created_at").type(JsonFieldType.STRING)
                                                                       .description("카테고리 생성일"),
                fieldWithPath("data.basket_list[].category.modified_at").type(JsonFieldType.STRING)
                                                                        .description("카테고리 수정일"))));
    }
}
