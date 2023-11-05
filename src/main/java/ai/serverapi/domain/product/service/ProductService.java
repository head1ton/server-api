package ai.serverapi.domain.product.service;

import ai.serverapi.config.base.MessageVo;
import ai.serverapi.config.security.TokenProvider;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.product.dto.ProductDto;
import ai.serverapi.domain.product.dto.PutProductDto;
import ai.serverapi.domain.product.entity.Category;
import ai.serverapi.domain.product.entity.Product;
import ai.serverapi.domain.product.repository.CategoryRepository;
import ai.serverapi.domain.product.repository.ProductCustomRepository;
import ai.serverapi.domain.product.repository.ProductRepository;
import ai.serverapi.domain.product.vo.CategoryListVo;
import ai.serverapi.domain.product.vo.CategoryVo;
import ai.serverapi.domain.product.vo.ProductListVo;
import ai.serverapi.domain.product.vo.ProductVo;
import ai.serverapi.domain.product.vo.SellerVo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final CategoryRepository categoryRepository;

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;

    public ProductVo postProduct(
        final ProductDto productDto,
        final HttpServletRequest request) {
        Long categoryId = productDto.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 카테고리입니다."));

        Member member = getMember(request);

        Product product = productRepository.save(Product.of(member, category, productDto));

        return ProductVo.productReturnVo(product);
    }

    private Member getMember(final HttpServletRequest request) {

        Long memberId = tokenProvider.getMemberId(request);

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));
        return member;
    }

    public ProductListVo getProductList(final Pageable pageable, final String search) {
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, 0L);

        return ProductListVo.builder()
                            .totalPage(page.getTotalPages())
                            .totalElements(page.getTotalElements())
                            .numberOfElements(page.getNumberOfElements())
                            .last(page.isLast())
                            .empty(page.isLast())
                            .list(page.getContent())
                            .build();
    }

    public ProductVo getProduct(final Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            throw new IllegalArgumentException("유효하지 않은 상품번호 입니다.");
        });

        return ProductVo.builder()
                        .id(product.getId())
                        .mainTitle(product.getMainTitle())
                        .mainExplanation(product.getMainExplanation())
                        .productMainExplanation(product.getProductMainExplanation())
                        .productSubExplanation(product.getProductSubExplanation())
                        .purchaseInquiry(product.getPurchaseInquiry())
                        .producer(product.getProducer())
                        .origin(product.getOrigin())
                        .originPrice(product.getOriginPrice())
                        .price(product.getPrice())
                        .mainImage(product.getMainImage())
                        .image1(product.getImage1())
                        .image2(product.getImage2())
                        .image3(product.getImage3())
                        .createdAt(product.getCreatedAt())
                        .modifiedAt(product.getModifiedAt())
                        .seller(SellerVo.builder()
                                        .name(product.getMember().getName())
                                        .email(product.getMember().getEmail())
                                        .nickname(product.getMember().getNickname())
                                        .memberId(product.getMember().getId())
                                        .build())
                        .category(CategoryVo.builder()
                                            .categoryId(product.getCategory().getId())
                                            .name(product.getCategory().getName())
                                            .createdAt(product.getCategory().getCreatedAt())
                                            .modifiedAt(product.getCategory().getModifiedAt())
                                            .build())
                        .build();
    }

    @Transactional
    public ProductVo putProduct(final PutProductDto putProductDto) {
        Long targetProductId = putProductDto.getId();

        Long categoryId = putProductDto.getCategoryId();

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다."));

        Product product = productRepository.findById(targetProductId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 상품입니다.")
        );

        product.put(putProductDto);
        product.putCategory(category);

        return ProductVo.productReturnVo(product);
    }

    public ProductListVo getProductListBySeller(
        final Pageable pageable,
        final String search,
        final HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, memberId);

        return ProductListVo.builder()
                            .totalPage(page.getTotalPages())
                            .totalElements(page.getTotalElements())
                            .numberOfElements(page.getNumberOfElements())
                            .last(page.isLast())
                            .empty(page.isLast())
                            .list(page.getContent())
                            .build();
    }

    public CategoryListVo getCategoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryVo> categoryVoList = new LinkedList<>();

        for (Category category : categoryList) {
            categoryVoList.add(CategoryVo.builder()
                                         .categoryId(category.getId())
                                         .name(category.getName())
                                         .createdAt(category.getCreatedAt())
                                         .modifiedAt(category.getModifiedAt())
                                         .build());
        }

        return CategoryListVo.builder()
                             .list(categoryVoList)
                             .build();
    }

    @Transactional
    public MessageVo addViewCnt(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다."));
        product.addViewCnt();
        return MessageVo.builder()
                        .message("조회수 증가 성공")
                        .build();
    }
}
