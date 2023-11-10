package ai.serverapi.product.controller;

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

import ai.serverapi.RestdocsBaseTest;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.response.LoginResponse;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.Option;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.dto.request.OptionRequest;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.dto.request.PutProductRequest;
import ai.serverapi.product.enums.OptionStatus;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.OptionRepository;
import ai.serverapi.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class SellerProductRestdocsDocs extends RestdocsBaseTest {

    private final static String PREFIX = "/api/seller/product";
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
    @Autowired
    private OptionRepository optionRepository;

    @Test
    @DisplayName(PREFIX + "(GET)")
    void getProductList() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse login = memberAuthService.login(loginRequest);

        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Member member2 = memberRepository.findByEmail(SELLER2_EMAIL).get();

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(null, "option1", 1000,
            OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);

        ProductRequest productRequest = new ProductRequest(1L, "다른 유저 상품", "메인 설명", "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal", 10, optionRequestList, "normal");
        ProductRequest searchDto = new ProductRequest(1L, "셀러 유저 상품", "메인 설명", "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal", 10, optionRequestList, "normal");

        Category category = categoryRepository.findById(1L).get();

        Seller seller = sellerRepository.findByMember(member).get();
        Seller seller2 = sellerRepository.findByMember(member2).get();

        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 25; i++) {
            productRepository.save(Product.of(seller2, category, productRequest));
        }

        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, searchDto));
        }

        ResultActions perform = mockMvc.perform(
            get(PREFIX)
                .param("search", "")
                .param("page", "0")
                .param("size", "5")
                .param("status", "normal")
                .param("category_id", "0")
                .header(AUTHORIZATION, "Bearer " + login.accessToken())
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
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)").optional(),
                parameterWithName("category_id").description("카테고리 검색 id (0:전체, 1:과일, 2:야채, 3:육류)")
                                                .optional()
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
                fieldWithPath("data.list[].product_id").type(JsonFieldType.NUMBER)
                                                       .description("등록 상품 id"),
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
                fieldWithPath("data.list[].ea").type(JsonFieldType.NUMBER).description("재고 수량"),
                fieldWithPath("data.list[].status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.list[].type").type(JsonFieldType.STRING)
                                                 .description("상품 type (일반:normal, option:옵션)"),
                fieldWithPath("data.list[].option_list").type(JsonFieldType.ARRAY)
                                                        .description("상품 option list"),
                fieldWithPath("data.list[].created_at").type(JsonFieldType.STRING)
                                                       .description("생성일"),
                fieldWithPath("data.list[].modified_at").type(JsonFieldType.STRING)
                                                        .description("수정일"),
                fieldWithPath("data.list[].seller.seller_id").type(JsonFieldType.NUMBER)
                                                             .description("판매자 id"),
                fieldWithPath("data.list[].seller.email").type(JsonFieldType.STRING)
                                                         .description("판매자 email"),
                fieldWithPath("data.list[].seller.company").type(JsonFieldType.STRING)
                                                           .description("판매자 회사"),
                fieldWithPath("data.list[].seller.zonecode").type(JsonFieldType.STRING)
                                                            .description("판매자 우편번호"),
                fieldWithPath("data.list[].seller.tel").type(JsonFieldType.STRING)
                                                       .description("판매자 연락처"),
                fieldWithPath("data.list[].seller.address").type(JsonFieldType.STRING)
                                                           .description("판매자 주소"),
                fieldWithPath("data.list[].seller.address_detail").type(JsonFieldType.STRING)
                                                                  .description("판매자 상세 주소"),
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
    @DisplayName(PREFIX + " (POST/normal)")
    void postProduct() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse login = memberAuthService.login(loginRequest);

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(null, "option1", 1000,
            OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);

        ProductRequest productRequest = new ProductRequest(1L, "메인 타이틀", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            9000, "취급 방법", "원산지", "공급자", "https://main_image", "https://image1", "https://image2",
            "https://image3", "normal", 10, null, "normal");

        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();

        ResultActions perform = mockMvc.perform(
            post(PREFIX)
                .header(AUTHORIZATION, "Bearer " + login.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(productRequest))
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (SELLER 권한 유저)")
            ),
            requestFields(
                fieldWithPath("category_id").type(JsonFieldType.NUMBER)
                                            .description("카테고리 id (1:과일, 2:채소, 3:육류)"),
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
                fieldWithPath("type").type(JsonFieldType.STRING).description("상품 타입 : normal / option"),
                fieldWithPath("main_image").type(JsonFieldType.STRING).description("메인 이미지"),
                fieldWithPath("image1").type(JsonFieldType.STRING).description("이미지1").optional(),
                fieldWithPath("image2").type(JsonFieldType.STRING).description("이미지2").optional(),
                fieldWithPath("image3").type(JsonFieldType.STRING).description("이미지3").optional(),
                fieldWithPath("ea").type(JsonFieldType.NUMBER).description("재고 수량").optional(),
                fieldWithPath("status").type(JsonFieldType.STRING)
                                       .description("상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("option_list[]").type(JsonFieldType.ARRAY).description("옵션").optional(),
                fieldWithPath("option_list[].name").type(JsonFieldType.STRING).description("옵션 이름"),
                fieldWithPath("option_list[].extraPrice").type(JsonFieldType.NUMBER).description("옵션 가격"),
                fieldWithPath("option_list[].ea").type(JsonFieldType.NUMBER).description("옵션 재고")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.product_id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
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
                fieldWithPath("data.ea").type(JsonFieldType.NUMBER).description("재고 수량"),
                fieldWithPath("data.status").type(JsonFieldType.STRING)
                                            .description("상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.type").type(JsonFieldType.STRING)
                                          .description("상품 type (일반:normal, option:옵션)"),
                fieldWithPath("data.option_list").type(JsonFieldType.ARRAY)
                                                 .description("상품 option list"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일"),
                fieldWithPath("data.seller.seller_id").type(JsonFieldType.NUMBER)
                                                      .description("판매자 id"),
                fieldWithPath("data.seller.email").type(JsonFieldType.STRING)
                                                  .description("판매자 email"),
                fieldWithPath("data.seller.company").type(JsonFieldType.STRING)
                                                    .description("판매자 회사명"),
                fieldWithPath("data.seller.zonecode").type(JsonFieldType.STRING)
                                                     .description("판매자 우편 주소"),
                fieldWithPath("data.seller.address").type(JsonFieldType.STRING)
                                                    .description("판매자 회사 주소"),
                fieldWithPath("data.seller.address_detail").type(JsonFieldType.STRING)
                                                           .description("판매자 상세 주소"),
                fieldWithPath("data.seller.tel").type(JsonFieldType.STRING)
                                                .description("판매자 회사 연락처"),
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
    @DisplayName(PREFIX + " (POST/option)")
    void postOptionProduct() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse login = memberAuthService.login(loginRequest);

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(null, "option1", 1000,
            OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);

        ProductRequest productRequest = new ProductRequest(1L, "메인 타이틀", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal", 10, optionRequestList, "option");
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX)
                .header(AUTHORIZATION, "Bearer " + login.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(productRequest))
        );
        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (SELLER 권한 유저)")
            ),
            requestFields(
                fieldWithPath("category_id").type(JsonFieldType.NUMBER)
                                            .description("카테고리 id (1:과일, 2:채소, 3:육류)"),
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
                fieldWithPath("type").type(JsonFieldType.STRING).description("상품 타입 : normal / option"),
                fieldWithPath("main_image").type(JsonFieldType.STRING).description("메인 이미지"),
                fieldWithPath("image1").type(JsonFieldType.STRING).description("이미지1").optional(),
                fieldWithPath("image2").type(JsonFieldType.STRING).description("이미지2").optional(),
                fieldWithPath("image3").type(JsonFieldType.STRING).description("이미지3").optional(),
                fieldWithPath("ea").type(JsonFieldType.NUMBER).description("재고 수량").optional(),
                fieldWithPath("status").type(JsonFieldType.STRING)
                                       .description("상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("option_list[]").type(JsonFieldType.ARRAY).description("옵션").optional(),
                fieldWithPath("option_list[].name").type(JsonFieldType.STRING).description("옵션 이름"),
                fieldWithPath("option_list[].extra_price").type(JsonFieldType.NUMBER)
                                                          .description("옵션 가격"),
                fieldWithPath("option_list[].status").type(JsonFieldType.STRING).description(
                    "옵션 상태 (일반 : normal, 삭제 : delete)"),
                fieldWithPath("option_list[].ea").type(JsonFieldType.NUMBER).description("옵션 재고")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.product_id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
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
                fieldWithPath("data.ea").type(JsonFieldType.NUMBER).description("재고 수량"),
                fieldWithPath("data.status").type(JsonFieldType.STRING)
                                            .description("상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.type").type(JsonFieldType.STRING)
                                          .description("상품 type (일반:normal, option:옵션)"),
                fieldWithPath("data.option_list").type(JsonFieldType.ARRAY)
                                                 .description("상품 option list"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일"),
                fieldWithPath("data.seller.seller_id").type(JsonFieldType.NUMBER)
                                                      .description("판매자 id"),
                fieldWithPath("data.seller.email").type(JsonFieldType.STRING)
                                                  .description("판매자 email"),
                fieldWithPath("data.seller.company").type(JsonFieldType.STRING)
                                                    .description("판매자 회사명"),
                fieldWithPath("data.seller.zonecode").type(JsonFieldType.STRING)
                                                     .description("판매자 우편 주소"),
                fieldWithPath("data.seller.address").type(JsonFieldType.STRING)
                                                    .description("판매자 회사 주소"),
                fieldWithPath("data.seller.address_detail").type(JsonFieldType.STRING)
                                                           .description("판매자 상세 주소"),
                fieldWithPath("data.seller.tel").type(JsonFieldType.STRING)
                                                .description("판매자 회사 연락처"),
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
    @DisplayName(PREFIX + " (PUT/normal)")
    void putProduct() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse login = memberAuthService.login(loginRequest);
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null,
            "normal");

        Seller seller = sellerRepository.findByMember(member).get();

        Product originalProduct = productRepository.save(Product.of(seller, category,
            productRequest));
        Long productId = originalProduct.getId();
        PutProductRequest putProductRequest = PutProductRequest.builder()
                                                               .productId(productId)
                                                               .categoryId(2L)
                                                               .mainTitle("수정된 제목")
                                                               .mainExplanation("수정된 설명")
                                                               .mainImage("https://mainImage")
                                                               .origin("원산지")
                                                               .purchaseInquiry("보관방법")
                                                               .producer("생산자")
                                                               .originPrice(12000)
                                                               .price(10000)
                                                               .productMainExplanation("상품 메인 설명")
                                                               .productSubExplanation("상품 보조 설명")
                                                               .image1("https://image1")
                                                               .image2("https://image2")
                                                               .image3("https://image3")
                                                               .status("normal")
                                                               .ea(10)
                                                               .build();

        ResultActions perform = mockMvc.perform(
            put(PREFIX)
                .header(AUTHORIZATION, "Bearer " + login.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(putProductRequest))
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (SELLER 권한 유저)")
            ),
            requestFields(
                fieldWithPath("product_id").type(JsonFieldType.NUMBER).description("상품 id"),
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
                fieldWithPath("ea").type(JsonFieldType.NUMBER).description("재고 수량"),
                fieldWithPath("status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반: normal, 숨김: hidden, 삭제: delete / 대소문자 구분 없음")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.product_id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
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
                fieldWithPath("data.ea").type(JsonFieldType.NUMBER).description("재고 수량"),
                fieldWithPath("data.status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.type").type(JsonFieldType.STRING)
                                          .description("상품 type (일반:normal, option:옵션)"),
                fieldWithPath("data.option_list").type(JsonFieldType.ARRAY)
                                                 .description("상품 option list"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING)
                                                .description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING)
                                                 .description("수정일"),
                fieldWithPath("data.seller.seller_id").type(JsonFieldType.NUMBER)
                                                      .description("판매자 id"),
                fieldWithPath("data.seller.email").type(JsonFieldType.STRING)
                                                  .description("판매자 email"),
                fieldWithPath("data.seller.company").type(JsonFieldType.STRING)
                                                    .description("판매자 회사명"),
                fieldWithPath("data.seller.zonecode").type(JsonFieldType.STRING)
                                                     .description("판매자 우편번호"),
                fieldWithPath("data.seller.address").type(JsonFieldType.STRING)
                                                    .description("판매자 회사 주소"),
                fieldWithPath("data.seller.address_detail").type(JsonFieldType.STRING)
                                                           .description("판매자 상세 주소"),
                fieldWithPath("data.seller.tel").type(JsonFieldType.STRING)
                                                .description("판매자 회사 연락처"),
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
    @DisplayName(PREFIX + " (PUT/option)")
    void putProductByOption() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse login = memberAuthService.login(loginRequest);
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null,
            "normal", 10, new ArrayList<>(), "option");
        Seller seller = sellerRepository.findByMember(member).get();
        Product originalProduct = productRepository.save(
            Product.of(seller, category, productRequest));
        Long productId = originalProduct.getId();

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(null, "option1", 1000,
            OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);

        Option option = optionRepository.save(Option.of(originalProduct, optionRequest1));
        Long optionId = option.getId();
        originalProduct.addOptionsList(option);

        List<OptionRequest> putOptionRequestList = new ArrayList<>();
        OptionRequest putOptionRequest1 = new OptionRequest(optionId, "option3333", 1000,
            OptionStatus.NORMAL.name(), 100);
        putOptionRequestList.add(putOptionRequest1);

        PutProductRequest putProductRequest = PutProductRequest.builder()
                                                               .productId(productId)
                                                               .categoryId(2L)
                                                               .mainTitle("수정된 제목")
                                                               .mainExplanation("수정된 설명")
                                                               .mainImage("https://mainImage")
                                                               .origin("원산지")
                                                               .purchaseInquiry("보관방법")
                                                               .producer("생산자")
                                                               .originPrice(12000)
                                                               .price(10000)
                                                               .productMainExplanation("상품 메인 설명")
                                                               .productSubExplanation("상품 보조 설명")
                                                               .image1("https://image1")
                                                               .image2("https://image2")
                                                               .image3("https://image3")
                                                               .status("normal")
                                                               .type("option")
                                                               .ea(10)
                                                               .optionList(putOptionRequestList)
                                                               .build();

        ResultActions resultActions = mockMvc.perform(
            put(PREFIX)
                .header(AUTHORIZATION, "Bearer " + login.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(putProductRequest))
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token (SELLER 권한 유저)")
            ),
            requestFields(
                fieldWithPath("product_id").type(JsonFieldType.NUMBER).description("상품 id"),
                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("카테고리 id"),
                fieldWithPath("main_title").type(JsonFieldType.STRING).description("제목"),
                fieldWithPath("main_explanation").type(JsonFieldType.STRING).description("메인 설명"),
                fieldWithPath("product_main_explanation").type(JsonFieldType.STRING)
                                                         .description("상품 메인 설명"),
                fieldWithPath("product_sub_explanation").type(JsonFieldType.STRING)
                                                        .description("상품 보조 설명"),
                fieldWithPath("origin_price").type(JsonFieldType.NUMBER).description("원가"),
                fieldWithPath("price").type(JsonFieldType.NUMBER).description("실제 판매가"),
                fieldWithPath("purchase_inquiry").type(JsonFieldType.STRING).description("취급 방법"),
                fieldWithPath("origin").type(JsonFieldType.STRING).description("원산지"),
                fieldWithPath("producer").type(JsonFieldType.STRING).description("공급자"),
                fieldWithPath("main_image").type(JsonFieldType.STRING).description("메인 이미지"),
                fieldWithPath("image1").type(JsonFieldType.STRING).description("이미지1"),
                fieldWithPath("image2").type(JsonFieldType.STRING).description("이미지2"),
                fieldWithPath("image3").type(JsonFieldType.STRING).description("이미지3"),
                fieldWithPath("ea").type(JsonFieldType.NUMBER).description("재고 수량"),
                fieldWithPath("status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반: normal, 숨김: hidden, 삭제: delete / 대소문자 구분 없음)"),
                fieldWithPath("type").type(JsonFieldType.STRING)
                                     .description("상품 상태값 (일반 : normal, 옵션 : option / 대소문자 구분 없음)"),
                fieldWithPath("option_list").type(JsonFieldType.ARRAY).description("상품 옵션 리스트"),
                fieldWithPath("option_list[].option_id").type(JsonFieldType.NUMBER)
                                                        .description("상품 옵션 id"),
                fieldWithPath("option_list[].name").type(JsonFieldType.STRING)
                                                   .description("상품 옵션 이름"),
                fieldWithPath("option_list[].extra_price").type(JsonFieldType.NUMBER)
                                                          .description("상품 옵션 가격"),
                fieldWithPath("option_list[].status").type(JsonFieldType.STRING).description(
                    "옵션 상태 (일반 : normal, 삭제 : delete)"),
                fieldWithPath("option_list[].ea").type(JsonFieldType.NUMBER).description("상품 옵션 재고")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.product_id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
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
                fieldWithPath("data.image1").type(JsonFieldType.STRING).description("이미지1")
                                            .optional(),
                fieldWithPath("data.image2").type(JsonFieldType.STRING).description("이미지2")
                                            .optional(),
                fieldWithPath("data.image3").type(JsonFieldType.STRING).description("이미지3")
                                            .optional(),
                fieldWithPath("data.view_cnt").type(JsonFieldType.NUMBER).description("조회수"),
                fieldWithPath("data.ea").type(JsonFieldType.NUMBER).description("재고 수량").optional(),
                fieldWithPath("data.status").type(JsonFieldType.STRING)
                                            .description(
                                                "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.type").type(JsonFieldType.STRING)
                                          .description("상품 type (일반:normal, option:옵션)"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일"),
                fieldWithPath("data.option_list").type(JsonFieldType.ARRAY)
                                                 .description("상품 option list"),
                fieldWithPath("data.option_list[].option_id").type(JsonFieldType.NUMBER)
                                                             .description("상품 option id"),
                fieldWithPath("data.option_list[].name").type(JsonFieldType.STRING)
                                                        .description("상품 option name"),
                fieldWithPath("data.option_list[].extra_price").type(JsonFieldType.NUMBER)
                                                               .description("상품 option price"),
                fieldWithPath("data.option_list[].ea").type(JsonFieldType.NUMBER)
                                                      .description("상품 option ea"),
                fieldWithPath("data.option_list[].created_at").type(JsonFieldType.STRING)
                                                              .description("생성일"),
                fieldWithPath("data.option_list[].modified_at").type(JsonFieldType.STRING)
                                                               .description("수정일"),
                fieldWithPath("data.seller.seller_id").type(JsonFieldType.NUMBER)
                                                      .description("판매자 id"),
                fieldWithPath("data.seller.email").type(JsonFieldType.STRING)
                                                  .description("판매자 email"),
                fieldWithPath("data.seller.company").type(JsonFieldType.STRING)
                                                    .description("판매자 회사명"),
                fieldWithPath("data.seller.zonecode").type(JsonFieldType.STRING)
                                                     .description("판매자 우편 주소"),
                fieldWithPath("data.seller.address").type(JsonFieldType.STRING)
                                                    .description("판매자 회사 주소"),
                fieldWithPath("data.seller.address_detail").type(JsonFieldType.STRING)
                                                           .description("판매자 상세 주소"),
                fieldWithPath("data.seller.tel").type(JsonFieldType.STRING)
                                                .description("판매자 회사 연락처"),
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