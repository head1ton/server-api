package ai.serverapi.product.service;

import ai.serverapi.global.base.MessageVo;
import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.Option;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.dto.request.AddViewCntRequest;
import ai.serverapi.product.dto.request.OptionRequest;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.dto.request.PutProductRequest;
import ai.serverapi.product.dto.response.CategoryListResponse;
import ai.serverapi.product.dto.response.CategoryResponse;
import ai.serverapi.product.dto.response.ProductBasketListResponse;
import ai.serverapi.product.dto.response.ProductListResponse;
import ai.serverapi.product.dto.response.ProductResponse;
import ai.serverapi.product.enums.OptionStatus;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.OptionRepository;
import ai.serverapi.product.repository.ProductCustomRepositoryImpl;
import ai.serverapi.product.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
public class ProductServiceImpl implements ProductService {

    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductCustomRepositoryImpl productCustomRepositoryImpl;
    private final OptionRepository optionRepository;

    @Transactional
    @Override
    public ProductResponse postProduct(
        final ProductRequest productRequest,
        final HttpServletRequest request) {
        Long categoryId = productRequest.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 카테고리입니다."));
        ProductType type = ProductType.valueOf(productRequest.getType().toUpperCase(Locale.ROOT));

        Member member = getMember(request);
        Seller seller = sellerRepository.findByMember(member).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 판매자입니다."));

        Product product = productRepository.save(Product.of(seller, category, productRequest));

        if (type == ProductType.NORMAL) {
            return new ProductResponse(product);
        }

        List<Option> optionList = new LinkedList<>();
        for (int i = 0; i < productRequest.getOptionList().size(); i++) {
            Option option = Option.of(product, productRequest.getOptionList().get(i));
            optionList.add(option);
        }
        optionRepository.saveAll(optionList);

        return new ProductResponse(product);
    }

    private Member getMember(final HttpServletRequest request) {

        Long memberId = tokenProvider.getMemberId(request);

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));
        return member;
    }

    @Override
    public ProductListResponse getProductList(
        final Pageable pageable,
        final String search,
        final String status,
        final Long categoryId,
        final Long sellerId) {

        ProductStatus productStatusOfEnums = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepositoryImpl.findAll(pageable, search,
            productStatusOfEnums,
            category, sellerId);

        return new ProductListResponse(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public ProductBasketListResponse getProductBasket(List<Long> productIdList) {
        List<ProductResponse> productList = productCustomRepositoryImpl.findAllByIdList(
            productIdList);
        return new ProductBasketListResponse(productList);
    }

    public ProductResponse getProduct(final Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            throw new IllegalArgumentException("유효하지 않은 상품번호 입니다.");
        });

        return new ProductResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse putProduct(final PutProductRequest putProductRequest) {
        Long targetProductId = putProductRequest.getProductId();

        Long categoryId = putProductRequest.getCategoryId();

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다."));

        Product product = productRepository.findById(targetProductId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 상품입니다.")
        );

        product.put(putProductRequest);
        product.putCategory(category);

        if (product.getType() == ProductType.OPTION) {

            List<Option> findOptionList = optionRepository.findByProductAndStatus(product,
                OptionStatus.NORMAL);
            List<OptionRequest> saveRequestOptionList = new LinkedList<>();

            for (OptionRequest optionRequest : putProductRequest.getOptionList()) {
                Long requestOptionId = Optional.ofNullable(optionRequest.getOptionId())
                                               .orElse(0L);
                Optional<Option> optionalOption = findOptionList.stream().filter(
                    option -> option.getId().equals(requestOptionId)).findFirst();

                if (optionalOption.isPresent()) {
                    Option option = optionalOption.get();
                    option.put(optionRequest);
                } else {
                    saveRequestOptionList.add(optionRequest);
                }
            }

            if (!saveRequestOptionList.isEmpty()) {
                List<Option> options = Option.ofList(product, saveRequestOptionList);
                optionRepository.saveAll(options);
                product.addAllOptionsList(options);
            }
        }

        return new ProductResponse(product);
    }

    @Override
    public ProductListResponse getProductListBySeller(
        final Pageable pageable,
        final String search,
        final String status,
        final Long categoryId,
        final HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        ProductStatus productStatusOfEnums = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepositoryImpl.findAll(pageable, search,
            productStatusOfEnums,
            category,
            memberId);

        return new ProductListResponse(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    @Override
    public CategoryListResponse getCategoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryResponse> categoryResponseList = new LinkedList<>();

        for (Category category : categoryList) {
            categoryResponseList.add(
                new CategoryResponse(category.getId(), category.getName(), category.getCreatedAt(),
                    category.getModifiedAt()));
        }

        return new CategoryListResponse(categoryResponseList);
    }

    @Transactional
    @Override
    public MessageVo addViewCnt(AddViewCntRequest addViewCntRequest) {
        Product product = productRepository.findById(addViewCntRequest.getProduct_id())
                                           .orElseThrow(() ->
                                               new IllegalArgumentException("유효하지 않은 상품입니다."));
        product.addViewCnt();
        return new MessageVo("조회수 증가 성공");
    }
}
