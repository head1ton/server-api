package ai.serverapi.product.service;

import ai.serverapi.global.base.MessageVo;
import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.repository.MemberJpaRepository;
import ai.serverapi.member.repository.SellerJpaRepository;
import ai.serverapi.product.controller.request.AddViewCntRequest;
import ai.serverapi.product.controller.request.OptionRequest;
import ai.serverapi.product.controller.request.ProductRequest;
import ai.serverapi.product.controller.request.PutProductRequest;
import ai.serverapi.product.controller.response.CategoryListResponse;
import ai.serverapi.product.controller.response.CategoryResponse;
import ai.serverapi.product.controller.response.ProductBasketListResponse;
import ai.serverapi.product.controller.response.ProductListResponse;
import ai.serverapi.product.controller.response.ProductResponse;
import ai.serverapi.product.domain.entity.CategoryEntity;
import ai.serverapi.product.domain.entity.OptionEntity;
import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.domain.entity.SellerEntity;
import ai.serverapi.product.enums.OptionStatus;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import ai.serverapi.product.repository.CategoryJpaRepository;
import ai.serverapi.product.repository.OptionJpaRepository;
import ai.serverapi.product.repository.ProductCustomJpaRepositoryImpl;
import ai.serverapi.product.repository.ProductJpaRepository;
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

    private final SellerJpaRepository sellerJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final TokenProvider tokenProvider;
    private final MemberJpaRepository memberJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductCustomJpaRepositoryImpl productCustomRepositoryImpl;
    private final OptionJpaRepository optionJpaRepository;

    @Transactional
    @Override
    public ProductResponse postProduct(
        final ProductRequest productRequest,
        final HttpServletRequest request) {
        Long categoryId = productRequest.getCategoryId();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 카테고리입니다."));
        ProductType type = ProductType.valueOf(productRequest.getType().toUpperCase(Locale.ROOT));

        MemberEntity memberEntity = getMember(request);
        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 판매자입니다."));

        ProductEntity productEntity = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));

        if (type == ProductType.NORMAL) {
            return ProductResponse.from(productEntity);
        }

        List<OptionEntity> optionEntityList = new LinkedList<>();
        for (int i = 0; i < productRequest.getOptionList().size(); i++) {
            OptionEntity optionEntity = OptionEntity.of(productEntity,
                productRequest.getOptionList().get(i));
            optionEntityList.add(optionEntity);
        }
        optionJpaRepository.saveAll(optionEntityList);

        return ProductResponse.from(productEntity);
    }

    private MemberEntity getMember(final HttpServletRequest request) {

        Long memberId = tokenProvider.getMemberId(request);

        MemberEntity memberEntity = memberJpaRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));
        return memberEntity;
    }

    @Override
    public ProductListResponse getProductList(
        final Pageable pageable,
        final String search,
        final String status,
        final Long categoryId,
        final Long sellerId) {

        ProductStatus productStatusOfEnums = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepositoryImpl.findAll(pageable, search,
            productStatusOfEnums,
            categoryEntity, sellerId);

        return ProductListResponse.from(page);
    }

    public ProductBasketListResponse getProductBasket(List<Long> productIdList) {
        List<ProductResponse> productList = productCustomRepositoryImpl.findAllByIdList(
            productIdList);
        return ProductBasketListResponse.builder().basketList(productList).build();
    }

    public ProductResponse getProduct(final Long id) {
        ProductEntity productEntity = productJpaRepository.findById(id).orElseThrow(() -> {
            throw new IllegalArgumentException("유효하지 않은 상품번호 입니다.");
        });

        return ProductResponse.from(productEntity);
    }

    @Transactional
    @Override
    public ProductResponse putProduct(final PutProductRequest putProductRequest) {
        Long targetProductId = putProductRequest.getProductId();

        Long categoryId = putProductRequest.getCategoryId();

        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다."));

        ProductEntity productEntity = productJpaRepository.findById(targetProductId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 상품입니다.")
        );

        productEntity.put(putProductRequest);
        productEntity.putCategory(categoryEntity);

        if (productEntity.getType() == ProductType.OPTION) {

            List<OptionEntity> findOptionListEntity = optionJpaRepository.findByProductAndStatus(
                productEntity,
                OptionStatus.NORMAL);
            List<OptionRequest> saveRequestOptionList = new LinkedList<>();

            for (OptionRequest optionRequest : putProductRequest.getOptionList()) {
                Long requestOptionId = Optional.ofNullable(optionRequest.getOptionId())
                                               .orElse(0L);
                Optional<OptionEntity> optionalOption = findOptionListEntity.stream().filter(
                    option -> option.getId().equals(requestOptionId)).findFirst();

                if (optionalOption.isPresent()) {
                    OptionEntity optionEntity = optionalOption.get();
                    optionEntity.put(optionRequest);
                } else {
                    saveRequestOptionList.add(optionRequest);
                }
            }

            if (!saveRequestOptionList.isEmpty()) {
                List<OptionEntity> optionEntities = OptionEntity.ofList(productEntity,
                    saveRequestOptionList);
                optionJpaRepository.saveAll(optionEntities);
                productEntity.addAllOptionsList(optionEntities);
            }
        }

        return ProductResponse.from(productEntity);
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
        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepositoryImpl.findAll(pageable, search,
            productStatusOfEnums,
            categoryEntity,
            memberId);

        return ProductListResponse.from(page);
    }

    @Override
    public CategoryListResponse getCategoryList() {
        List<CategoryEntity> categoryEntityList = categoryJpaRepository.findAll();
        List<CategoryResponse> categoryResponseList = new LinkedList<>();

        for (CategoryEntity categoryEntity : categoryEntityList) {
            categoryResponseList.add(
                CategoryResponse.builder()
                                .categoryId(categoryEntity.getId())
                                .name(categoryEntity.getName())
                                .createdAt(categoryEntity.getCreatedAt())
                                .modifiedAt(categoryEntity.getModifiedAt())
                                .build());
        }

        return CategoryListResponse.builder().list(categoryResponseList).build();
    }

    @Transactional
    @Override
    public MessageVo addViewCnt(AddViewCntRequest addViewCntRequest) {
        ProductEntity productEntity = productJpaRepository.findById(
                                                              addViewCntRequest.getProductId())
                                                          .orElseThrow(() ->
                                               new IllegalArgumentException("유효하지 않은 상품입니다."));
        productEntity.addViewCnt();
        return new MessageVo("조회수 증가 성공");
    }
}
