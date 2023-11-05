package ai.serverapi.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.config.base.MessageVo;
import ai.serverapi.domain.member.dto.LoginDto;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.entity.Seller;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.repository.SellerRepository;
import ai.serverapi.domain.member.service.MemberAuthService;
import ai.serverapi.domain.member.vo.LoginVo;
import ai.serverapi.domain.product.dto.AddViewCntDto;
import ai.serverapi.domain.product.dto.ProductDto;
import ai.serverapi.domain.product.dto.PutProductDto;
import ai.serverapi.domain.product.entity.Category;
import ai.serverapi.domain.product.entity.Product;
import ai.serverapi.domain.product.repository.CategoryRepository;
import ai.serverapi.domain.product.repository.ProductRepository;
import ai.serverapi.domain.product.vo.ProductListVo;
import ai.serverapi.domain.product.vo.ProductVo;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@TestInstance(Lifecycle.PER_CLASS)
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

    @BeforeAll
    void setUp() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Optional<Seller> optionalSeller = sellerRepository.findByMember(member);
        if (optionalSeller.isEmpty()) {
            sellerRepository.save(
                Seller.of(member, "회사명", "01012344321", "회사 주소", "mail@gmail.com"));
        }
        Member member2 = memberRepository.findByEmail("seller2@gmail.com").get();
        Optional<Seller> optionalSeller2 = sellerRepository.findByMember(member2);
        if (optionalSeller2.isEmpty()) {
            sellerRepository.save(
                Seller.of(member2, "회사명", "01012344321", "회사 주소", "mail@gmail.com"));
        }
    }

    @Test
    @DisplayName("상품 리스트 불러오기")
    void getProductListSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Long categoryId = 1L;

        ProductDto productDto = new ProductDto(categoryId, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        ProductDto searchDto = new ProductDto(categoryId, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        Category category = categoryRepository.findById(categoryId).get();

        Seller seller = sellerRepository.findByMember(member).get();

        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 25; i++) {
            productRepository.save(Product.of(seller, category, productDto));
        }
        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();

        ProductListVo searchList = productService.getProductList(pageable, "검색", "normal",
            categoryId);

        assertThat(searchList.list().stream().findFirst().get().mainTitle()).contains("검색");
    }

    @Test
    @DisplayName("상품 리스트 판매자 계정 조건으로 불러오기")
    void getProductListSuccess2() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Member member2 = memberRepository.findByEmail("seller2@gmail.com").get();
        LoginDto loginDto = new LoginDto("seller@gmail.com", "password");
        LoginVo login = memberAuthService.login(loginDto);
        Long categoryId = 1L;

        Category category = categoryRepository.findById(categoryId).get();

        ProductDto productDto = new ProductDto(categoryId, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        ProductDto searchDto = new ProductDto(categoryId, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();
        Seller seller2 = sellerRepository.findByMember(member2).get();

        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, productDto));
        }
        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller2, category, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();

        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        ProductListVo searchList = productService.getProductListBySeller(pageable, "", "normal",
            categoryId, request);

        assertThat(
            searchList.list().stream().findFirst().get().seller().sellerId()).isEqualTo(
            seller.getId());
    }

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess() {
        LoginDto loginDto = new LoginDto("seller@gmail.com", "password");
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());
        ProductDto productDto = new ProductDto(1L, "메인 타이틀", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal");

        ProductVo productVo = productService.postProduct(productDto, request);

        Optional<Product> byId = productRepository.findById(productVo.id());

        assertThat(byId).isNotEmpty();
    }
    
    @Test
    @DisplayName("상품 수정 성공")
    void putProductSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Category category = categoryRepository.findById(1L).get();

        ProductDto productDto = new ProductDto(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal");

        Seller seller = sellerRepository.findByMember(member).get();

        Product originalProduct = productRepository.save(Product.of(seller, category, productDto));
        String originalProductMainTitle = originalProduct.getMainTitle();
        Long productId = originalProduct.getId();
        Long categoryId = originalProduct.getCategory().getId();

        PutProductDto putProductDto = new PutProductDto(
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

        productService.putProduct(putProductDto);

        Product changeProduct = productRepository.findById(productId).get();
        assertThat(changeProduct.getMainTitle()).isNotEqualTo(originalProductMainTitle);
    }

    @Test
    @DisplayName("상품 조회수 증가 성공")
    void addViewCntSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Category category = categoryRepository.findById(1L).get();
        ProductDto productDto = new ProductDto(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();

        Product product = productRepository.save(Product.of(seller, category, productDto));

        MessageVo messageVo = productService.addViewCnt(new AddViewCntDto(product.getId()));

        assertThat(messageVo.message()).contains("조회수 증가 성공");
        assertThat(product.getViewCnt()).isEqualTo(1);
    }


}
