package ai.serverapi.domain.product.service;

import ai.serverapi.config.base.MessageVo;
import ai.serverapi.config.security.TokenProvider;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.entity.Seller;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.repository.SellerRepository;
import ai.serverapi.domain.product.dto.AddViewCntDto;
import ai.serverapi.domain.product.dto.ProductDto;
import ai.serverapi.domain.product.dto.PutProductDto;
import ai.serverapi.domain.product.entity.Category;
import ai.serverapi.domain.product.entity.Product;
import ai.serverapi.domain.product.enums.Status;
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
import java.util.Locale;
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

    private final SellerRepository sellerRepository;
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

        Seller seller = sellerRepository.findByMember(member).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 판매자입니다."));

        Product product = productRepository.save(Product.of(seller, category, productDto));

        return new ProductVo(product);
    }

    private Member getMember(final HttpServletRequest request) {

        Long memberId = tokenProvider.getMemberId(request);

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));
        return member;
    }

    public ProductListVo getProductList(final Pageable pageable, final String search,
        String status, Long categoryId) {
        Status statusOfEnums = Status.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, statusOfEnums,
            category, 0L);

        return new ProductListVo(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public ProductVo getProduct(final Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            throw new IllegalArgumentException("유효하지 않은 상품번호 입니다.");
        });

        return new ProductVo(product,
            new SellerVo(product.getSeller().getId(), product.getSeller().getEmail(),
                product.getSeller().getCompany(), product.getSeller().getAddress(),
                product.getSeller().getTel()));
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

        return new ProductVo(product);
    }

    public ProductListVo getProductListBySeller(
        final Pageable pageable,
        final String search,
        final String status,
        final Long categoryId,
        final HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Status statusOfEnums = Status.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, statusOfEnums,
            category,
            memberId);

        return new ProductListVo(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public CategoryListVo getCategoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryVo> categoryVoList = new LinkedList<>();

        for (Category category : categoryList) {
            categoryVoList.add(
                new CategoryVo(category.getId(), category.getName(), category.getCreatedAt(),
                    category.getModifiedAt()));
        }

        return new CategoryListVo(categoryVoList);
    }

    @Transactional
    public MessageVo addViewCnt(AddViewCntDto addViewCntDto) {
        Product product = productRepository.findById(addViewCntDto.getProduct_id())
                                           .orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다."));
        product.addViewCnt();
        return new MessageVo("조회수 증가 성공");
    }
}
