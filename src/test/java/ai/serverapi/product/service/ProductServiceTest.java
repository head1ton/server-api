package ai.serverapi.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.BaseTest;
import ai.serverapi.global.base.MessageVo;
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
import ai.serverapi.product.dto.request.PutProductRequest;
import ai.serverapi.product.dto.response.ProductListResponse;
import ai.serverapi.product.dto.response.ProductResponse;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
class ProductServiceTest extends BaseTest {

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

    @Test
    @DisplayName("상품 리스트 불러오기")
    void getProductListSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Long categoryId = 1L;

        ProductRequest productRequest = new ProductRequest(categoryId, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        ProductRequest searchDto = new ProductRequest(categoryId, "검색 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        Category category = categoryRepository.findById(categoryId).get();

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
            categoryId, 0L);

        assertThat(searchList.list().stream().findFirst().get().mainTitle()).contains("검색");
    }

    @Test
    @DisplayName("상품 리스트 판매자 계정 조건으로 불러오기")
    void getProductListSuccess2() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Member member2 = memberRepository.findByEmail("seller2@gmail.com").get();
        LoginRequest loginRequest = new LoginRequest("seller@gmail.com", "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        Long categoryId = 1L;

        Category category = categoryRepository.findById(categoryId).get();

        ProductRequest productRequest = new ProductRequest(categoryId, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        ProductRequest searchDto = new ProductRequest(categoryId, "검색 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

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
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        ProductListResponse searchList = productService.getProductListBySeller(pageable, "",
            "normal",
            categoryId, request);

        assertThat(
            searchList.list().stream().findFirst().get().seller().sellerId()).isEqualTo(
            seller.getId());
    }

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess() {
        LoginRequest loginRequest = new LoginRequest("seller@gmail.com", "password");
        LoginResponse login = memberAuthService.login(loginRequest);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        ProductRequest productRequest = new ProductRequest(1L, "메인 타이틀", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal");

        ProductResponse productResponse = productService.postProduct(productRequest, request);

        Optional<Product> byId = productRepository.findById(productResponse.id());

        assertThat(byId).isNotEmpty();
    }
    
    @Test
    @DisplayName("상품 수정 성공")
    void putProductSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal");

        Seller seller = sellerRepository.findByMember(member).get();

        Product originalProduct = productRepository.save(Product.of(seller, category,
            productRequest));
        String originalProductMainTitle = originalProduct.getMainTitle();
        Long productId = originalProduct.getId();
        Long categoryId = originalProduct.getCategory().getId();

        PutProductRequest putProductRequest = new PutProductRequest(
            productId,
            2L,
            "수정된 제목",
            "수정된 설명",
            "상품 메인 설명",
            "상품 서브 설명",
            12000,
            10000,
            "보관 방법",
            "원산지",
            "생산자",
            "https://mainImage",
            null, null, null,
            "normal");

        productService.putProduct(putProductRequest);

        Product changeProduct = productRepository.findById(productId).get();
        assertThat(changeProduct.getMainTitle()).isNotEqualTo(originalProductMainTitle);
    }

    @Test
    @DisplayName("상품 조회수 증가 성공")
    void addViewCntSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Category category = categoryRepository.findById(1L).get();
        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();

        Product product = productRepository.save(Product.of(seller, category, productRequest));

        MessageVo messageVo = productService.addViewCnt(new AddViewCntRequest(product.getId()));

        assertThat(messageVo.message()).contains("조회수 증가 성공");
        assertThat(product.getViewCnt()).isEqualTo(1);
    }


}
