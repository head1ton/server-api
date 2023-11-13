package ai.serverapi.product.controller;

import static ai.serverapi.Base.CATEGORY_ID_BEAUTY;
import static ai.serverapi.Base.PASSWORD;
import static ai.serverapi.Base.SELLER_EMAIL;
import static ai.serverapi.Base.objectMapper;
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

import ai.serverapi.RestdocsBaseTest;
import ai.serverapi.member.controller.request.LoginRequest;
import ai.serverapi.member.controller.response.LoginResponse;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.repository.MemberJpaRepository;
import ai.serverapi.member.repository.SellerJpaRepository;
import ai.serverapi.member.service.MemberAuthServiceImpl;
import ai.serverapi.product.controller.request.AddViewCntRequest;
import ai.serverapi.product.controller.request.OptionRequest;
import ai.serverapi.product.controller.request.ProductRequest;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional(readOnly = true)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Execution(ExecutionMode.CONCURRENT)
class ProductControllerDocs extends RestdocsBaseTest {

    private static final String PREFIX = "/api/product";

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
        String mainTitle = "검색";

        //given
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_BEAUTY).get();
        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = OptionRequest.builder()
                                                    .name("option1")
                                                    .extraPrice(1000)
                                                    .status(OptionStatus.NORMAL.name())
                                                    .ea(100)
                                                    .build();

        optionRequestList.add(optionRequest1);

        ProductRequest productRequest1 = ProductRequest.builder()
                                                       .categoryId(CATEGORY_ID_BEAUTY)
                                                       .mainTitle(mainTitle)
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
                                                       .optionList(optionRequestList)
                                                       .type("option")
                                                       .build();

        ProductRequest productRequest2 = ProductRequest.builder()
                                                       .categoryId(CATEGORY_ID_BEAUTY)
                                                       .mainTitle(mainTitle + " 일반 상품")
                                                       .mainExplanation("일반 상품 메인 설명")
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
        productJpaRepository.save(ProductEntity.of(sellerEntity, categoryEntity, productRequest2));

        for (int i = 0; i < 25; i++) {
            ProductEntity productEntity = productJpaRepository.save(
                ProductEntity.of(sellerEntity, categoryEntity, productRequest1));
            for (int j = 0; j < optionRequestList.size(); j++) {
                OptionEntity optionEntity = optionJpaRepository.save(
                    OptionEntity.of(productEntity, optionRequestList.get(j)));
                productEntity.addOptionsList(optionEntity);
            }
        }

        for (int i = 0; i < 10; i++) {
            ProductEntity productEntity = productJpaRepository.save(
                ProductEntity.of(sellerEntity, categoryEntity, productRequest2));
            for (int j = 0; j < optionRequestList.size(); j++) {
                OptionEntity optionEntity = optionJpaRepository.save(
                    OptionEntity.of(productEntity, optionRequestList.get(j)));
                productEntity.addOptionsList(optionEntity);
            }
        }
        //when
        ResultActions perform = mock.perform(
            get(PREFIX)
                .param("search", mainTitle)
                .param("page", "0")
                .param("size", "5")
                .param("status", "normal")
                .param("category_id", "0")
                .param("seller_id", "0")
        );
        //then
        perform.andExpect(status().is2xxSuccessful());
        perform.andDo(docs.document(
            queryParameters(
                parameterWithName("page").description("paging 시작 페이지 번호").optional(),
                parameterWithName("size").description("paging 시작 페이지 기준 개수 크기").optional(),
                parameterWithName("search").description("검색어").optional(),
                parameterWithName("status").description(
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)").optional(),
                parameterWithName("category_id").description("카테고리 검색 id (0:전체, 1:과일, 2:야채, 3:육류)")
                                                .optional(),
                parameterWithName("seller_id").description(
                    "판매자 id (seller_id로 검색시 판매자가 등록한 상품만 반환됨)").optional()
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
                fieldWithPath("data.list[].product_id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
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
                fieldWithPath("data.list[].status").type(JsonFieldType.STRING)
                                                   .description("상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.list[].type").type(JsonFieldType.STRING)
                                                 .description("상품 type (일반:product, 옵션:option)"),
                fieldWithPath("data.list[].option_list").type(JsonFieldType.ARRAY)
                                                        .description("상품 option list"),
                fieldWithPath("data.list[].option_list[].option_id").type(JsonFieldType.NUMBER)
                                                                    .description("상품 option id"),
                fieldWithPath("data.list[].option_list[].name").type(JsonFieldType.STRING)
                                                               .description("상품 option명"),
                fieldWithPath("data.list[].option_list[].extra_price").type(JsonFieldType.NUMBER)
                                                                      .description("상품 option 추가 금액"),
                fieldWithPath("data.list[].option_list[].ea").type(JsonFieldType.NUMBER)
                                                             .description("상품 option 재고"),
                fieldWithPath("data.list[].option_list[].created_at").type(JsonFieldType.STRING)
                                                                     .description("상품 option 등록일"),
                fieldWithPath("data.list[].option_list[].modified_at").type(JsonFieldType.STRING)
                                                                      .description("상품 option 수정일"),
                fieldWithPath("data.list[].created_at").type(JsonFieldType.STRING)
                                                       .description("등록일"),
                fieldWithPath("data.list[].modified_at").type(JsonFieldType.STRING)
                                                        .description("수정일"),
                fieldWithPath("data.list[].seller.seller_id").type(JsonFieldType.NUMBER)
                                                             .description("판매자 id"),
                fieldWithPath("data.list[].seller.email").type(JsonFieldType.STRING)
                                                         .description("판매자 email"),
                fieldWithPath("data.list[].seller.company").type(JsonFieldType.STRING)
                                                           .description("판매자 회사명"),
                fieldWithPath("data.list[].seller.tel").type(JsonFieldType.STRING)
                                                       .description("판매자 연락처"),
                fieldWithPath("data.list[].seller.zonecode").type(JsonFieldType.STRING)
                                                            .description("판매자 우편 주소"),
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
    @DisplayName(PREFIX + "/{id}")
    void getProduct() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse login = memberAuthService.login(loginRequest);

        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_BEAUTY).get();

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

        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();
        ProductEntity productEntity = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));

        ResultActions perform = mock.perform(get(PREFIX + "/{id}", productEntity.getId()));

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            pathParameters(
                parameterWithName("id").description("상품 id")
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
                fieldWithPath("data.option_list").type(JsonFieldType.ARRAY).description("상품 option list"),
                fieldWithPath("data.type").type(JsonFieldType.STRING).description("상품 type (일반: product, 옵션: option)"),
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
        ResultActions perform = mock.perform(
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
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_BEAUTY).get();

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

        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();

        ProductEntity productEntity = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));

        ResultActions resultActions = mock.perform(
            patch(PREFIX + "/cnt")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                    AddViewCntRequest.builder().productId(productEntity.getId()).build()))
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
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_BEAUTY).get();

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

        ProductRequest productRequest2 = ProductRequest.builder()
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

        ProductRequest productRequest3 = ProductRequest.builder()
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
        ProductEntity productEntity1 = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));
        ProductEntity productEntity2 = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest2));
        ProductEntity productEntity3 = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest3));

        optionJpaRepository.save(new OptionEntity("옵션1", 1000, 100, productEntity1));
        optionJpaRepository.save(new OptionEntity("옵션2", 1000, 100, productEntity2));

        ResultActions perform = mock.perform(
                                        get(PREFIX + "/basket").param("product_id", productEntity3.getId().toString())
                                                               .param("product_id", productEntity1.getId().toString())
                                                               .param("product_id", productEntity2.getId().toString()))
                                    .andDo(print());

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            queryParameters(parameterWithName("product_id").description("상품 id").optional()),
            responseFields(fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.basket_list[]").type(JsonFieldType.ARRAY)
                                                   .description("등록 상품 list"),
                fieldWithPath("data.basket_list[].product_id").type(JsonFieldType.NUMBER)
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
                fieldWithPath("data.basket_list[].ea").type(JsonFieldType.NUMBER)
                                                      .description("재고 수량"),
                fieldWithPath("data.basket_list[].status").type(JsonFieldType.STRING).description(
                    "상품 상태값 (일반:normal, 숨김:hidden, 삭제:delete / 대소문자 구분 없음)"),
                fieldWithPath("data.basket_list[].type").type(JsonFieldType.STRING)
                                                        .description("상품 type (일반:product, 옵션:option)"),
                fieldWithPath("data.basket_list[].option_list").type(JsonFieldType.ARRAY)
                                                               .description("상품 option list"),
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
                                                                        .description("카테고리 수정일")
//                fieldWithPath("data.basket_list[].option_list[].option_id").type(JsonFieldType.NUMBER)
//                                                                           .description("옵션 id"),
//                fieldWithPath("data.basket_list[].option_list[].name").type(JsonFieldType.STRING)
//                                                                      .description("옵션명"),
//                fieldWithPath("data.basket_list[].option_list[].extra_price").type(JsonFieldType.NUMBER)
//                                                                             .description("옵션 추가금액"),
//                fieldWithPath("data.basket_list[].option_list[].ea").type(JsonFieldType.NUMBER)
//                                                                    .description("재고량"),
//                fieldWithPath("data.basket_list[].option_list[].created_at").type(JsonFieldType.STRING)
//                                                                            .description("재고 등록일"),
//                fieldWithPath("data.basket_list[].option_list[].modified_at").type(JsonFieldType.STRING)
//                                                                             .description("재고 수정일")
            )
        ));
    }
}
