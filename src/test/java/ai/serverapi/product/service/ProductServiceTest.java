package ai.serverapi.product.service;

import static ai.serverapi.Base.CATEGORY_ID_BEAUTY;
import static ai.serverapi.Base.CATEGORY_ID_HEALTH;
import static ai.serverapi.Base.SELLER2_EMAIL;
import static ai.serverapi.Base.SELLER_EMAIL;
import static ai.serverapi.Base.SELLER_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.global.base.MessageVo;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.dto.request.AddViewCntRequest;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.dto.request.PutProductRequest;
import ai.serverapi.product.dto.response.ProductListResponse;
import ai.serverapi.product.dto.response.ProductResponse;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional(readOnly = true)
class ProductServiceTest {

    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SellerRepository sellerRepository;

    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        sellerRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 리스트 불러오기")
    void getProductListSuccess() {
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();

        ProductRequest productRequest = new ProductRequest(CATEGORY_ID_BEAUTY, "메인 제목", "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");

        ProductRequest searchDto = new ProductRequest(CATEGORY_ID_BEAUTY, "검색 제목", "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");

        Category category = categoryRepository.findById(CATEGORY_ID_BEAUTY).get();

        Seller seller = sellerRepository.findByMember(member).get();

        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 25; i++) {
            productRepository.save(Product.of(seller, category, productRequest));
        }
        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();

        ProductListResponse searchList = productService.getProductList(pageable, "검색", "normal",
            CATEGORY_ID_BEAUTY, 0L);

        assertThat(searchList.list().stream().findFirst().get().getMainTitle()).contains("검색");
    }

    @Test
    @DisplayName("상품 리스트 판매자 계정 조건으로 불러오기")
    void getProductListSuccess2() {
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Member member2 = memberRepository.findByEmail(SELLER2_EMAIL).get();

        Long categoryId = CATEGORY_ID_BEAUTY;

        Category category = categoryRepository.findById(categoryId).get();

        ProductRequest productRequest = new ProductRequest(categoryId, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");

        ProductRequest searchDto = new ProductRequest(categoryId, "검색 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();
        Seller seller2 = sellerRepository.findByMember(member2).get();

        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, productRequest));
        }
        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller2, category, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.accessToken());

        ProductListResponse searchList = productService.getProductListBySeller(pageable, "",
            "normal",
            categoryId, request);

        assertThat(
            searchList.list().stream().findFirst().get().getSeller().getSellerId()).isEqualTo(
            seller.getId());
    }

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess() {

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.accessToken());

        ProductRequest productRequest = new ProductRequest(CATEGORY_ID_BEAUTY, "메인 타이틀", "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명", 10000,
            9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal", 10, null, "normal");

        ProductResponse productResponse = productService.postProduct(productRequest, request);

        Optional<Product> byId = productRepository.findById(productResponse.getProductId());

        assertThat(byId).isNotEmpty();
    }
    
    @Test
    @DisplayName("상품 수정 성공")
    void putProductSuccess() {
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null,
            "normal");
        Seller seller = sellerRepository.findByMember(member).get();
        Product originalProduct = productRepository.save(
            Product.of(seller, category, productRequest));
        String originalProductMainTitle = originalProduct.getMainTitle();
        Long productId = originalProduct.getId();
        PutProductRequest putProductRequest = PutProductRequest.builder()
                                                               .productId(productId)
                                                               .categoryId(CATEGORY_ID_HEALTH)
                                                               .mainTitle("수정된 제목")
                                                               .mainExplanation("수정된 설명")
                                                               .mainImage("https://mainImage")
                                                               .origin("원산지")
                                                               .purchaseInquiry("보관방법")
                                                               .producer("생산자")
                                                               .originPrice(12000)
                                                               .price(10000)
                                                               .image1("https://image1")
                                                               .image2("https://image2")
                                                               .image3("https://image3")
                                                               .status("normal")
                                                               .ea(10)
                                                               .build();
        // when
        productService.putProduct(putProductRequest);
        // then
        Product changeProduct = productRepository.findById(productId).get();
        assertThat(changeProduct.getMainTitle()).isNotEqualTo(originalProductMainTitle);
    }

    @Test
    @DisplayName("상품 조회수 증가 성공")
    void addViewCntSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Category category = categoryRepository.findById(CATEGORY_ID_BEAUTY).get();
        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();

        Product product = productRepository.save(Product.of(seller, category, productRequest));

        MessageVo messageVo = productService.addViewCnt(new AddViewCntRequest(product.getId()));

        assertThat(messageVo.message()).contains("조회수 증가 성공");
        assertThat(product.getViewCnt()).isEqualTo(1);
    }


}
