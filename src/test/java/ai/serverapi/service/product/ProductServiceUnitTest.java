package ai.serverapi.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.dto.product.PutProductDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.product.Product;
import ai.serverapi.domain.enums.Role;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.repository.member.MemberRepository;
import ai.serverapi.repository.product.ProductRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

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
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 등록 성공")
    void postProduct() {

        String mainTitle = "메인 제목";
        request.addHeader(AUTHORIZATION, "Bearer token");

        ProductDto productDto = new ProductDto(
            mainTitle, "메인 설명",
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

        BDDMockito.given(tokenProvider.resolveToken(any()))
                  .willReturn("token");
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@gmail.com", "password", "nickname", "name",
            "19941030", Role.SELLER, null, null, now, now);
        BDDMockito.given(memberRepository.findById(any())).willReturn(Optional.of(member));

        BDDMockito.given(productRepository.save(any())).willReturn(Product.of(member, productDto));

        ProductVo productVo = productService.postProduct(productDto, request);

        assertThat(productVo.getMainTitle()).isEqualTo(mainTitle);
    }

    @Test
    @DisplayName("상품 조회에 실패")
    void getProductFail() {
        Throwable throwable = catchThrowable(() -> productService.getProduct(0L));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 상품");
    }

    @Test
    @DisplayName("수정하려는 상품이 존재하지 않는 경우 실패")
    void putProductFail1() {
        PutProductDto dto = new PutProductDto(0L, null, null, null, null, 0, 0,
            null, null, null, null, null, null, null);
        BDDMockito.given(productRepository.findById(any())).willReturn(Optional.ofNullable(null));

        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 상품");
    }

//    @Test
//    @DisplayName("수정하려는 상품이 존재하지만 회원의 정보가 없을 경우엔 실패")
//    void putProductFail2() {
//        PatchProductDto dto = new PatchProductDto(1L, null, null, null, null, 0, 0, null, null,
//            null, null, null, null, null);
//
//        BDDMockito.given(tokenProvider.resolveToken(any())).willReturn("token");
//        LocalDateTime now = LocalDateTime.now();
//        Member member = new Member(1L, "email@mail.com", "password", "nickname", "name",
//            "19941030", Role.SELLER, null, null, now, now);
//
//        ProductDto productDto = new ProductDto();
//        BDDMockito.given(productRepository.findById(any())).willReturn(Optional.ofNullable(Product.of(member, productDto)));
//        BDDMockito.given(memberRepository.findById(any())).willReturn(Optional.ofNullable(null));
//
//        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));
//        System.out.println("throwable = " + throwable);
////        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
////            .hasMessageContaining("유효하지 않은 회원");
//    }
}
