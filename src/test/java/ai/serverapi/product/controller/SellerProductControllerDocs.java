package ai.serverapi.product.controller;

import static ai.serverapi.Base.CATEGORY_ID_BEAUTY;
import static ai.serverapi.Base.SELLER2_EMAIL;
import static ai.serverapi.Base.SELLER_EMAIL;
import static ai.serverapi.Base.SELLER_LOGIN;
import static ai.serverapi.Base.objectMapper;
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
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.repository.MemberJpaRepository;
import ai.serverapi.member.repository.SellerJpaRepository;
import ai.serverapi.member.service.MemberAuthServiceImpl;
import ai.serverapi.product.controller.request.OptionRequest;
import ai.serverapi.product.controller.request.ProductRequest;
import ai.serverapi.product.controller.request.PutProductRequest;
import ai.serverapi.product.domain.entity.CategoryEntity;
import ai.serverapi.product.domain.entity.OptionEntity;
import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.domain.entity.SellerEntity;
import ai.serverapi.product.enums.OptionStatus;
import ai.serverapi.product.repository.CategoryJpaRepository;
import ai.serverapi.product.repository.OptionJpaRepository;
import ai.serverapi.product.repository.ProductJpaRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Execution(ExecutionMode.CONCURRENT)
class SellerProductControllerDocs extends RestdocsBaseTest {

    private final static String PREFIX = "/api/seller/product";
    @Autowired
    private MemberAuthServiceImpl memberAuthService;
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private CategoryJpaRepository categoryJpaRepository;
    @Autowired
    private SellerJpaRepository sellerJpaRepository;
    @Autowired
    private OptionJpaRepository optionJpaRepository;

    @AfterEach
    void cleanUp() {
        optionJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
        categoryJpaRepository.deleteAll();
        sellerJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName(PREFIX + "(GET)")
    void getProductList() throws Exception {

        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        MemberEntity memberEntity2 = memberJpaRepository.findByEmail(SELLER2_EMAIL).get();

        List<OptionRequest> optionRequestList = new ArrayList<>();

        OptionRequest optionRequest1 = OptionRequest.builder()
                                                    .name("option1")
                                                    .extraPrice(1000)
                                                    .status(OptionStatus.NORMAL.name())
                                                    .ea(100)
                                                    .build();
        optionRequestList.add(optionRequest1);

        ProductRequest productRequest = ProductRequest.builder()
                                                      .categoryId(CATEGORY_ID_BEAUTY)
                                                      .mainTitle("메인 제목")
                                                      .mainExplanation("메인 설명")
                                                      .productMainExplanation("상품 메인 설명")
                                                      .productSubExplanation("상품 서브 설명")
                                                      .originPrice(10000)
                                                      .price(8000)
                                                      .purchaseInquiry("보관 방법")
                                                      .origin("원산지")
                                                      .producer("생산자")
                                                      .mainImage("https://mainImage")
                                                      .image1("https://image1")
                                                      .image2("https://image2")
                                                      .image3("https://image3")
                                                      .status("normal")
                                                      .ea(10)
                                                      .type("normal")
                                                      .build();

        ProductRequest searchDto = ProductRequest.builder()
                                                 .categoryId(CATEGORY_ID_BEAUTY)
                                                 .mainTitle("메인 제목")
                                                 .mainExplanation("메인 설명")
                                                 .productMainExplanation("상품 메인 설명")
                                                 .productSubExplanation("상품 서브 설명")
                                                 .originPrice(10000)
                                                 .price(8000)
                                                 .purchaseInquiry("보관 방법")
                                                 .origin("원산지")
                                                 .producer("생산자")
                                                 .mainImage("https://mainImage")
                                                 .image1("https://image1")
                                                 .image2("https://image2")
                                                 .image3("https://image3")
                                                 .status("normal")
                                                 .ea(10)
                                                 .type("option")
                                                 .optionList(optionRequestList)
                                                 .build();

        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_BEAUTY).get();

        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();
        SellerEntity sellerEntity2 = sellerJpaRepository.findByMember(memberEntity2).get();

        productJpaRepository.save(ProductEntity.of(sellerEntity, categoryEntity, searchDto));

        for (int i = 0; i < 25; i++) {
            productJpaRepository.save(
                ProductEntity.of(sellerEntity2, categoryEntity, productRequest));
        }

        for (int i = 0; i < 10; i++) {
            productJpaRepository.save(ProductEntity.of(sellerEntity, categoryEntity, searchDto));
        }

        ResultActions perform = mock.perform(
            get(PREFIX)
                .param("search", "")
                .param("page", "0")
                .param("size", "5")
                .param("status", "normal")
                .param("category_id", "0")
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
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
        ProductRequest productRequest = ProductRequest.builder()
                                                      .categoryId(CATEGORY_ID_BEAUTY)
                                                      .mainTitle("메인 제목")
                                                      .mainExplanation("메인 설명")
                                                      .productMainExplanation("상품 메인 설명")
                                                      .productSubExplanation("상품 서브 설명")
                                                      .originPrice(10000)
                                                      .price(8000)
                                                      .purchaseInquiry("보관 방법")
                                                      .origin("원산지")
                                                      .producer("생산자")
                                                      .mainImage("https://mainImage")
                                                      .image1("https://image1")
                                                      .image2("https://image2")
                                                      .image3("https://image3")
                                                      .status("normal")
                                                      .ea(10)
                                                      .type("normal")
                                                      .build();

        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();

        ResultActions perform = mock.perform(
            post(PREFIX)
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
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

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = OptionRequest.builder()
                                                    .name("option1")
                                                    .extraPrice(1000)
                                                    .status(OptionStatus.NORMAL.name())
                                                    .ea(100)
                                                    .build();
        optionRequestList.add(optionRequest1);

        ProductRequest productRequest = ProductRequest.builder()
                                                      .categoryId(CATEGORY_ID_BEAUTY)
                                                      .mainTitle("메인 제목")
                                                      .mainExplanation("메인 설명")
                                                      .productMainExplanation("상품 메인 설명")
                                                      .productSubExplanation("상품 서브 설명")
                                                      .originPrice(10000)
                                                      .price(8000)
                                                      .purchaseInquiry("보관 방법")
                                                      .origin("원산지")
                                                      .producer("생산자")
                                                      .mainImage("https://mainImage")
                                                      .image1("https://image1")
                                                      .image2("https://image2")
                                                      .image3("https://image3")
                                                      .status("normal")
                                                      .ea(10)
                                                      .type("option")
                                                      .optionList(optionRequestList)
                                                      .build();

        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();

        ResultActions resultActions = mock.perform(
            post(PREFIX)
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
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

        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_BEAUTY).get();

        ProductRequest productRequest = ProductRequest.builder()
                                                      .categoryId(CATEGORY_ID_BEAUTY)
                                                      .mainTitle("메인 제목")
                                                      .mainExplanation("메인 설명")
                                                      .productMainExplanation("상품 메인 설명")
                                                      .productSubExplanation("상품 서브 설명")
                                                      .originPrice(10000)
                                                      .price(8000)
                                                      .purchaseInquiry("보관 방법")
                                                      .origin("원산지")
                                                      .producer("생산자")
                                                      .mainImage("https://mainImage")
                                                      .image1("https://image1")
                                                      .image2("https://image2")
                                                      .image3("https://image3")
                                                      .status("normal")
                                                      .ea(10)
                                                      .type("normal")
                                                      .build();

        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();

        ProductEntity originalProductEntity = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity,
            productRequest));
        Long productId = originalProductEntity.getId();
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
                                                               .type("normal")
                                                               .ea(10)
                                                               .build();

        ResultActions perform = mock.perform(
            put(PREFIX)
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
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
                    "상품 상태값 (일반: normal, 숨김: hidden, 삭제: delete / 대소문자 구분 없음"),
                fieldWithPath("type").type(JsonFieldType.STRING)
                                     .description("상품 type (일반:normal, 옵션:option)")
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

        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_BEAUTY).get();

        ProductRequest productRequest = ProductRequest.builder()
                                                      .categoryId(CATEGORY_ID_BEAUTY)
                                                      .mainTitle("메인 제목")
                                                      .mainExplanation("메인 설명")
                                                      .productMainExplanation("상품 메인 설명")
                                                      .productSubExplanation("상품 서브 설명")
                                                      .originPrice(10000)
                                                      .price(8000)
                                                      .purchaseInquiry("보관 방법")
                                                      .origin("원산지")
                                                      .producer("생산자")
                                                      .mainImage("https://mainImage")
                                                      .image1("https://image1")
                                                      .image2("https://image2")
                                                      .image3("https://image3")
                                                      .status("normal")
                                                      .ea(10)
                                                      .type("option")
                                                      .optionList(new ArrayList<>())
                                                      .build();

        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();
        ProductEntity originalProductEntity = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));
        Long productId = originalProductEntity.getId();

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = OptionRequest.builder()
                                                    .name("option1")
                                                    .extraPrice(1000)
                                                    .status(OptionStatus.NORMAL.name())
                                                    .ea(100)
                                                    .build();
        optionRequestList.add(optionRequest1);

        OptionEntity optionEntity = optionJpaRepository.save(
            OptionEntity.of(originalProductEntity, optionRequest1));
        Long optionId = optionEntity.getId();
        originalProductEntity.addOptionsList(optionEntity);

        List<OptionRequest> putOptionRequestList = new ArrayList<>();
        OptionRequest putOptionRequest1 = OptionRequest.builder()
                                                       .optionId(optionId)
                                                       .name("option3333")
                                                       .extraPrice(1000)
                                                       .status(OptionStatus.NORMAL.name())
                                                       .ea(100)
                                                       .build();
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

        ResultActions resultActions = mock.perform(
            put(PREFIX)
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
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
