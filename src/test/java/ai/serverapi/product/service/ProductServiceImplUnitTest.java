package ai.serverapi.product.service;

import static ai.serverapi.Base.PRODUCT_ID_MASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.enums.MemberRole;
import ai.serverapi.member.repository.MemberJpaRepository;
import ai.serverapi.member.repository.SellerJpaRepository;
import ai.serverapi.product.controller.request.AddViewCntRequest;
import ai.serverapi.product.controller.request.OptionRequest;
import ai.serverapi.product.controller.request.ProductRequest;
import ai.serverapi.product.controller.request.PutProductRequest;
import ai.serverapi.product.controller.response.ProductResponse;
import ai.serverapi.product.domain.entity.CategoryEntity;
import ai.serverapi.product.domain.entity.OptionEntity;
import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.domain.entity.SellerEntity;
import ai.serverapi.product.domain.model.Category;
import ai.serverapi.product.domain.model.Option;
import ai.serverapi.product.enums.OptionStatus;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import ai.serverapi.product.repository.CategoryJpaRepository;
import ai.serverapi.product.repository.OptionJpaRepository;
import ai.serverapi.product.repository.ProductCustomJpaRepositoryImpl;
import ai.serverapi.product.repository.ProductJpaRepository;
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
    private ProductJpaRepository productJpaRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private MemberJpaRepository memberJpaRepository;
    @Mock
    private CategoryJpaRepository categoryJpaRepository;
    @Mock
    private SellerJpaRepository sellerJpaRepository;
    @Mock
    private ProductCustomJpaRepositoryImpl productCustomRepositoryImpl;
    @Mock
    private OptionJpaRepository optionJpaRepository;
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
        CategoryEntity categoryEntity = CategoryEntity.from(Category.builder().build());

//        given(tokenProvider.resolveToken(any())).willReturn("token");
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        LocalDateTime now = LocalDateTime.now();
        MemberEntity memberEntity = new MemberEntity(1L, "email@gmail.com", "password", "nickname",
            "name",
            "19941030",
            MemberRole.SELLER, null, null, now, now);
        SellerEntity sellerEntity = SellerEntity.of(memberEntity, "회사명", "01012344321", "1234",
            "회사 주소", "상세 주소",
            "mail@gmail.com");
        given(memberJpaRepository.findById(any())).willReturn(Optional.of(memberEntity));
        given(sellerJpaRepository.findByMember(any())).willReturn(Optional.of(sellerEntity));
        given(productJpaRepository.save(any())).willReturn(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));
        given(categoryJpaRepository.findById(anyLong())).willReturn(
            Optional.of(CategoryEntity.from(Category.builder()
                                                    .build())));
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
        CategoryEntity categoryEntity = CategoryEntity.from(Category.builder().build());

//        given(tokenProvider.resolveToken(any())).willReturn("token");
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        LocalDateTime now = LocalDateTime.now();
        MemberEntity memberEntity = new MemberEntity(1L, "email@gmail.com", "password", "nickname",
            "name",
            "19941030", MemberRole.SELLER, null, null, now, now);
        SellerEntity sellerEntity = SellerEntity.of(memberEntity, "회사명", "01012341234", "1234",
            "회사 주소", "상세 주소",
            "mail@gmail.com");

        given(memberJpaRepository.findById(any())).willReturn(Optional.of(memberEntity));
        given(sellerJpaRepository.findByMember(any())).willReturn(Optional.of(sellerEntity));
        given(productJpaRepository.save(any())).willReturn(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));
        given(categoryJpaRepository.findById(anyLong())).willReturn(
            Optional.of(CategoryEntity.from(Category.builder().build())));

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

        given(categoryJpaRepository.findById(anyLong()))
            .willReturn(Optional.of(CategoryEntity.from(Category.builder().build())));
        given(productJpaRepository.findById(any())).willReturn(Optional.ofNullable(null));

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

        MemberEntity memberEntity = new MemberEntity(1L, "email@gmail.com", "password", "nickname",
            "name",
            "19941030", MemberRole.SELLER, null, null, now, now);
        SellerEntity sellerEntity = SellerEntity.of(memberEntity, "회사명", "01012341234", "1234",
            "회사 주소", "상세 주소",
            "mail@gmail.com");
        CategoryEntity categoryEntity = CategoryEntity.from(Category.builder().build());
        ProductEntity productEntity = ProductEntity.builder()
                                                   .id(PRODUCT_ID_MASK)
                                                   .seller(sellerEntity)
                                                   .category(categoryEntity)
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
        productEntity.addAllOptionsList(new ArrayList<>());

        Option optionBuild = Option.builder()
                                   .id(1L)
                                   .name(optionRequest1.getName())
                                   .extraPrice(optionRequest1.getExtraPrice())
                                   .ea(optionRequest1.getEa())
                                   .status(OptionStatus.NORMAL)
                                   .createdAt(now)
                                   .modifiedAt(now)
                                   .build();
        OptionEntity optionEntity = OptionEntity.from(optionBuild);
        productEntity.addOptionsList(optionEntity);

        given(categoryJpaRepository.findById(anyLong())).willReturn(Optional.of(categoryEntity));
        given(productJpaRepository.findById(any())).willReturn(Optional.of(productEntity));

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
