package ai.serverapi.service.product;

import static org.assertj.core.api.Assertions.assertThat;

import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.entity.product.Product;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.repository.product.ProductRepository;
import ai.serverapi.service.member.MemberAuthService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

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


}
