package ai.serverapi.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.config.base.MessageVo;
import ai.serverapi.domain.member.dto.LoginDto;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.record.LoginRecord;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.service.MemberAuthService;
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

    @Test
    @DisplayName("상품 등록 성공")
    void getProductSuccess() {
        String email = "seller@gmail.com";
        String password = "password";
        LoginDto loginDto = new LoginDto(email, password);
        LoginRecord loginRecord = memberAuthService.login(loginDto);

        request.addHeader("Authorization", "Bearer " + loginRecord.accessToken());

        ProductDto productDto = new ProductDto(
            1L,
            "메인 타이틀",
            "메인 설명",
            "상품 메인 설명",
            "상품 서브 설명",
            10000,
            9000,
            "취급 방법",
            "원산지",
            "공급자",
            "https://main_image",
            "https://image1",
            "https://image2",
            "https://image3",
            "normal");

        ProductVo productVo = productService.postProduct(productDto, request);

        Optional<Product> productId = productRepository.findById(productVo.getId());
        Product product = productId.get();
        String memberEmail = product.getMember().getEmail();

        assertThat(productId).isNotEmpty();
        assertThat(memberEmail).isEqualTo(loginDto.getEmail());
    }

    @Test
    @DisplayName("상품 리스트 불러오기")
    void getProductListSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();

        ProductDto productDto = new ProductDto(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        ProductDto searchDto = new ProductDto(1L, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        Category category = categoryRepository.findById(1L).get();

        productRepository.save(Product.of(member, category, searchDto));

        for (int i = 0; i < 25; i++) {
            productRepository.save(Product.of(member, category, productDto));
        }
        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(member, category, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();

        ProductListVo searchList = productService.getProductList(pageable, "검색", "normal");

        assertThat(searchList.getList().stream().findFirst().get().getMainTitle()).contains("검색");
    }

    @Test
    @DisplayName("상품 리스트 판매자 계정 조건으로 불러오기")
    void getProductListSuccess2() {
        Member seller = memberRepository.findByEmail("seller@gmail.com").get();
        Member seller2 = memberRepository.findByEmail("seller2@gmail.com").get();
        LoginDto loginDto = new LoginDto("seller@gmail.com", "password");
        LoginRecord login = memberAuthService.login(loginDto);

        ProductDto productDto = new ProductDto(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        ProductDto searchDto = new ProductDto(1L, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        Category category = categoryRepository.findById(1L).get();

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
            request);

        assertThat(
            searchList.getList().stream().findFirst().get().getSeller().getMemberId()).isEqualTo(
            seller.getId());
    }
    
    @Test
    @DisplayName("상품 수정 성공")
    void putProductSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        Category category = categoryRepository.findById(1L).get();

        ProductDto productDto = new ProductDto(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal");
        Product originalProduct = productRepository.save(Product.of(member, category, productDto));
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

        Product product = productRepository.save(Product.of(member, category, productDto));

        MessageVo messageVo = productService.addViewCnt(new AddViewCntDto(product.getId()));

        assertThat(messageVo.getMessage()).contains("조회수 증가 성공");
        assertThat(product.getViewCnt()).isEqualTo(1);
    }


}
