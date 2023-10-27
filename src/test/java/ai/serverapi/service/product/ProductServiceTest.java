package ai.serverapi.service.product;

import static org.assertj.core.api.Assertions.assertThat;

import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.entity.product.Product;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.repository.product.ProductRepository;
import ai.serverapi.service.member.MemberAuthService;
import java.util.Optional;
import org.assertj.core.api.Assertions;
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
    public void postProduct() {
        String email = "seller@gmail.com";
        String password = "password";
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberAuthService.login(loginDto);

        request.addHeader("Authorization", "Bearer " + loginVo.getAccessToken());

        Long saveProductId = productService.postProduct(request);

        Optional<Product> productId = productRepository.findById(saveProductId);
        Product product = productId.get();
        String memberEmail = product.getMember().getEmail();

        assertThat(productId).isNotEmpty();
        assertThat(memberEmail).isEqualTo(loginDto.getEmail());

    }

}
