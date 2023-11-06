package ai.serverapi.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.entity.Member;
import ai.serverapi.member.domain.entity.Seller;
import ai.serverapi.member.domain.enums.Role;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.product.domain.dto.AddViewCntDto;
import ai.serverapi.product.domain.dto.ProductDto;
import ai.serverapi.product.domain.dto.PutProductDto;
import ai.serverapi.product.domain.entity.Category;
import ai.serverapi.product.domain.entity.Product;
import ai.serverapi.product.domain.vo.ProductVo;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
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
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private Environment env;

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess1() {

        String mainTitle = "메인 제목";
        request.addHeader(AUTHORIZATION, "Bearer token");

        ProductDto productDto = new ProductDto(1L, mainTitle, "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal");
        Category category = new Category();
//        given(tokenProvider.resolveToken(any())).willReturn("token");
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "member@gmail.com", "password", "nickname", "name",
            "19941030", Role.SELLER, null, null, now, now);

        Seller seller = Seller.of(member, "회사명", "01012344321", "1234", "회사 주소", "mail@gmail.com");

        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(sellerRepository.findByMember(any())).willReturn(Optional.of(seller));
        given(productRepository.save(any())).willReturn(Product.of(seller, category, productDto));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(new Category()));

        ProductVo productVo = productService.postProduct(productDto, request);

        assertThat(productVo.mainTitle()).isEqualTo(mainTitle);
    }

    @Test
    @DisplayName("상품 카테고리가 존재하지 않아 실패")
    void postProductFail1() {
        request.addHeader(AUTHORIZATION, "Bearer token");
        String mainTitle = "메인 제목";

        ProductDto productDto = new ProductDto(0L, mainTitle, "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal");

        Throwable throwable = catchThrowable(() -> productService.postProduct(productDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 카테고리");
    }

    @Test
    @DisplayName("상품 조회에 실패")
    void getProductFail() {
        Throwable throwable = catchThrowable(() -> productService.getProduct(0L));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 상품");
    }

    @Test
    @DisplayName("수정하려는 상품의 카테고리가 존재하지 않는 경우 실패")
    void putProductFail1() {
        PutProductDto dto = new PutProductDto(0L, 0L, null, null, null, null, 0, 0,
            null, null, null, null, null, null, null, "normal");

        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 카테고리");
    }

    @Test
    @DisplayName("수정하려는 상품이 존재하지 않는 경우 실패")
    void putProductFail2() {
        PutProductDto dto = new PutProductDto(0L, 1L, null, null, null, null, 0, 0,
            null, null, null, null, null, null, null, "normal");

        given(categoryRepository.findById(anyLong()))
                  .willReturn(Optional.of(new Category()));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(null));

        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 상품");
    }

    @Test
    @DisplayName("존재하지 않는 상품은 조회수 증가에 실패")
    void addViewCnt() {
        Throwable throwable = catchThrowable(
            () -> productService.addViewCnt(new AddViewCntDto(1L)));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 상품");
    }
}
