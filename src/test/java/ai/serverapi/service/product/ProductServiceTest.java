package ai.serverapi.service.product;

import static org.assertj.core.api.Assertions.assertThat;

import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.dto.product.PutProductDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.product.Product;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.domain.vo.product.ProductListVo;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.repository.member.MemberRepository;
import ai.serverapi.repository.product.ProductRepository;
import ai.serverapi.service.member.MemberAuthService;
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

    @Test
    @DisplayName("상품 등록 성공")
    void postProduct() {
        String email = "seller@gmail.com";
        String password = "password";
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberAuthService.login(loginDto);

        request.addHeader("Authorization", "Bearer " + loginVo.getAccessToken());

        ProductDto productDto = new ProductDto(
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
            "https://image3");

        ProductVo productVo = productService.postProduct(productDto, request);

        Optional<Product> productId = productRepository.findById(productVo.getId());
        Product product = productId.get();
        String memberEmail = product.getMember().getEmail();

        assertThat(productId).isNotEmpty();
        assertThat(memberEmail).isEqualTo(loginDto.getEmail());
    }

    @Test
    @DisplayName("상품 리스트 불러오기")
    void getProductList() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();

        ProductDto productDto = new ProductDto("메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null);

        ProductDto searchDto = new ProductDto("검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null);

        productRepository.save(Product.of(member, searchDto));

        for (int i = 0; i < 25; i++) {
            productRepository.save(Product.of(member, productDto));
        }
        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(member, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();

        ProductListVo searchList = productService.getProductList(pageable, "검색");

        assertThat(searchList.getList().stream().findFirst().get().getMainTitle()).contains("검색");
    }

    @Test
    @DisplayName("상품 수정 성공")
    void putProductSuccess() {
        Member member = memberRepository.findByEmail("seller@gmail.com").get();
        ProductDto productDto = new ProductDto("메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3");
        Product originalProduct = productRepository.save(Product.of(member, productDto));
        String originalProductMainTitle = originalProduct.getMainTitle();
        Long productId = originalProduct.getId();

        PutProductDto putProductDto = new PutProductDto(
            productId,
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
            null, null, null);

        productService.putProduct(putProductDto);

        Product changeProduct = productRepository.findById(productId).get();
        assertThat(changeProduct.getMainTitle()).isNotEqualTo(originalProductMainTitle);
    }


}
