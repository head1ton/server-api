package ai.serverapi.product.service;

import static ai.serverapi.Base.PRODUCT_ID_MASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.enums.Role;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.Option;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.dto.request.AddViewCntRequest;
import ai.serverapi.product.dto.request.OptionRequest;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.dto.request.PutProductRequest;
import ai.serverapi.product.dto.response.ProductResponse;
import ai.serverapi.product.enums.OptionStatus;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.OptionRepository;
import ai.serverapi.product.repository.ProductCustomRepositoryImpl;
import ai.serverapi.product.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplUnitTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private ProductCustomRepositoryImpl productCustomRepositoryImpl;
    @Mock
    private OptionRepository optionRepository;
    @Mock
    private Environment env;

    @BeforeEach
    void setUp() {
        request.addHeader(AUTHORIZATION, "Bearer token");
    }

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess1() {
        //given
        String mainTitle = "메인 제목";

        ProductRequest productRequest = new ProductRequest(1L, mainTitle, "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal", 10, null, "normal");
        Category category = new Category();

//        given(tokenProvider.resolveToken(any())).willReturn("token");
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@gmail.com", "password", "nickname", "name",
            "19941030",
            Role.SELLER, null, null, now, now);
        Seller seller = Seller.of(member, "회사명", "01012344321", "1234", "회사 주소", "상세 주소",
            "mail@gmail.com");
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(sellerRepository.findByMember(any())).willReturn(Optional.of(seller));
        given(productRepository.save(any())).willReturn(Product.of(seller, category, productRequest));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(new Category()));
        //when
        ProductResponse productResponse = productService.postProduct(productRequest, request);
        //then
        assertThat(productResponse.getMainTitle()).isEqualTo(mainTitle);
    }

    @Test
    @DisplayName("옵션 상품 등록 성공")
    void postProductSuccess2() {
        String mainTitle = "메인 제목";

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(null, "option1", 1000,
            OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);

        ProductRequest productRequest = new ProductRequest(1L, mainTitle, "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1",
            "https://image2", "https://image3", "normal", 10, optionRequestList, "option");
        Category category = new Category();

//        given(tokenProvider.resolveToken(any())).willReturn("token");
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@gmail.com", "password", "nickname", "name",
            "19941030", Role.SELLER, null, null, now, now);
        Seller seller = Seller.of(member, "회사명", "01012341234", "1234", "회사 주소", "상세 주소",
            "mail@gmail.com");

        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(sellerRepository.findByMember(any())).willReturn(Optional.of(seller));
        given(productRepository.save(any())).willReturn(
            Product.of(seller, category, productRequest));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(new Category()));

        ProductResponse productResponse = productService.postProduct(productRequest, request);

        assertThat(productResponse.getMainTitle()).isEqualTo(mainTitle);
    }

    @Test
    @DisplayName("상품 카테고리가 존재하지 않아 실패")
    void postProductFail1() {
        String mainTitle = "메인 제목";

        ProductRequest productRequest = new ProductRequest(0L, mainTitle, "메인 설명", "상품 메인 설명",
            "상품 서브 설명",
            10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
            "https://image3", "normal", 10, null, "normal");

        Throwable throwable = catchThrowable(
            () -> productService.postProduct(productRequest, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 카테고리");
    }

    @Test
    @DisplayName("상품 타입이 존재하지 않아 실패")
    void postProductFail2() {
        String mainTitle = "메인 제목";

        ProductRequest productRequest = new ProductRequest(0L, mainTitle, "메인 설명", "상품 메인 설명",
            "상품 서브 설명", 10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1",
            "https://image2", "https://image3", "normal", 10, null, "normal");

        Throwable throwable = catchThrowable(
            () -> productService.postProduct(productRequest, request));

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
        PutProductRequest dto = PutProductRequest.builder()
                                                 .status("normal")
                                                 .build();

        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 카테고리");
    }

    @Test
    @DisplayName("수정하려는 상품이 존재하지 않는 경우 실패")
    void putProductFail2() {
        PutProductRequest dto = PutProductRequest.builder()
                                                 .categoryId(1L)
                                                 .status("normal")
                                                 .build();

        given(categoryRepository.findById(anyLong()))
                  .willReturn(Optional.of(new Category()));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(null));

        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 상품");
    }

    @Test
    @DisplayName("옵션 상품 수정 성공")
    void putProductSuccess1() {
        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(2L, "option2", 1000,
            OptionStatus.NORMAL.name(), 100);
        OptionRequest optionRequest2 = new OptionRequest(null, "option2", 1000,
            OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);
        optionRequestList.add(optionRequest2);
        LocalDateTime now = LocalDateTime.now();

        Member member = new Member(1L, "email@gmail.com", "password", "nickname", "name",
            "19941030", Role.SELLER, null, null, now, now);
        Seller seller = Seller.of(member, "회사명", "01012341234", "1234", "회사 주소", "상세 주소",
            "mail@gmail.com");
        Category category = new Category();
        Product product = Product.builder()
                                 .id(PRODUCT_ID_MASK)
                                 .seller(seller)
                                 .category(category)
                                 .mainTitle("메인 제목")
                                 .mainExplanation("메인 설명")
                                 .productMainExplanation("상품 메인 설명")
                                 .productSubExplanation("상품 서브 설명")
                                 .price(10000)
                                 .originPrice(9000)
                                 .mainImage("https://메인이미지")
                                 .image1("https://image1")
                                 .image2("https://image2")
                                 .image3("https://image3")
                                 .status(ProductStatus.NORMAL)
                                 .createdAt(now)
                                 .modifiedAt(now)
                                 .type(ProductType.OPTION)
                                 .build();
        product.addAllOptionsList(new ArrayList<>());

        Option option = new Option(1L, optionRequest1.getName(), optionRequest1.getExtraPrice(),
            optionRequest1.getEa(), OptionStatus.NORMAL, now, now, product);
        product.addOptionsList(option);

        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        PutProductRequest dto = PutProductRequest.builder()
                                                 .productId(1L)
                                                 .categoryId(1L)
                                                 .status("normal")
                                                 .ea(10)
                                                 .optionList(optionRequestList)
                                                 .build();

        ProductResponse productResponse = productService.putProduct(dto);

        assertThat(productResponse.getOptionList().get(0).getName()).isEqualTo(
            optionRequestList.get(0).getName());
    }

    @Test
    @DisplayName("존재하지 않는 상품은 조회수 증가에 실패")
    void addViewCnt() {
        Throwable throwable = catchThrowable(
            () -> productService.addViewCnt(new AddViewCntRequest(1L)));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 상품");
    }
}
