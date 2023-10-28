package ai.serverapi.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.common.s3.S3Service;
import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.product.Product;
import ai.serverapi.domain.enums.Role;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.repository.member.MemberRepository;
import ai.serverapi.repository.product.ProductRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    @InjectMocks
    private ProductService productService;

    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private Environment env;
    @Mock
    private S3Service s3Service;
    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 등록 성공")
    void postProduct() {

        request.addHeader(AUTHORIZATION, "Bearer token");

        List<MultipartFile> files = new LinkedList<>();
        String fileName1 = "test1.txt";
        String fileName2 = "test2.txt";
        String fileName3 = "test3.txt";
        String fileName4 = "test4.txt";
        String mainTitle = "메인 제목";

        files.add(new MockMultipartFile("test1", fileName1, StandardCharsets.UTF_8.name(),
            "abcd".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", fileName2, StandardCharsets.UTF_8.name(),
            "222".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", fileName3, StandardCharsets.UTF_8.name(),
            "3".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test4", fileName4, StandardCharsets.UTF_8.name(),
            "5".getBytes(StandardCharsets.UTF_8)));

        ProductDto dto = ProductDto.builder()
                                   .mainTitle(mainTitle)
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

        BDDMockito.given(tokenProvider.resolveToken(any()))
                  .willReturn("token");
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@gmail.com", "password", "nickname", "name",
            "19941030", Role.SELLER, null, null, now, now);
        BDDMockito.given(memberRepository.findById(any())).willReturn(Optional.of(member));
        BDDMockito.given(env.getProperty(anyString())).willReturn("https://s3.aw.url");

        List<String> list = new LinkedList<>();
        list.add(fileName1);
        list.add(fileName2);
        list.add(fileName3);
        list.add(fileName4);
        BDDMockito.given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);
        BDDMockito.given(productRepository.save(any())).willReturn(Product.of(member, dto));

        ProductVo productVo = productService.postProduct(dto, files, request);

        assertThat(productVo.getMainTitle()).isEqualTo(mainTitle);
    }
}
