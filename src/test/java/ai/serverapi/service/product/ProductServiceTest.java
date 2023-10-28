package ai.serverapi.service.product;

import static org.assertj.core.api.Assertions.assertThat;

import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.entity.product.Product;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.repository.product.ProductRepository;
import ai.serverapi.service.member.MemberAuthService;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

        List<MultipartFile> files = new LinkedList<>();
        files.add(new MockMultipartFile("test1", "test1.txt", StandardCharsets.UTF_8.name(),
            "abcd".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", "test2.txt", StandardCharsets.UTF_8.name(),
            "222".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", "test3.txt", StandardCharsets.UTF_8.name(),
            "3".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test4", "test4.txt", StandardCharsets.UTF_8.name(),
            "5".getBytes(StandardCharsets.UTF_8)));

        ProductDto dto = ProductDto.builder()
                                   .mainTitle("메인 제목")
                                   .mainExplanation("메인 설명")
                                   .productMainExplanation("상품 메인 설명")
                                   .productSubExplanation("상품 서브 설명")
                                   .producer("공급자")
                                   .origin("원산지")
                                   .purchaseInquiry("취급 방법")
                                   .originPrice(1000)
                                   .price(100)
                                   .mainImage(null)
                                   .image1(null)
                                   .image2(null)
                                   .image3(null)
                                   .build();

        ProductVo productVo = productService.postProduct(dto, files, request);

        Optional<Product> productId = productRepository.findById(productVo.getId());
        Product product = productId.get();
        String memberEmail = product.getMember().getEmail();

        assertThat(productId).isNotEmpty();
        assertThat(memberEmail).isEqualTo(loginDto.getEmail());

    }


}
