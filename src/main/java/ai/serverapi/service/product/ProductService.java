package ai.serverapi.service.product;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.product.Product;
import ai.serverapi.domain.vo.product.ProductListVo;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.domain.vo.product.SellerVo;
import ai.serverapi.repository.member.MemberRepository;
import ai.serverapi.repository.product.ProductCustomRepository;
import ai.serverapi.repository.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
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

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;

    public ProductVo postProduct(
        final ProductDto productDto,
        final HttpServletRequest request) {

        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));

        Product product = productRepository.save(Product.of(member, productDto));

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
                        .mainImage(productDto.getMainImage())
                        .image1(product.getImage1())
                        .image2(product.getImage2())
                        .image3(product.getImage3())
                        .build();
    }

    public ProductListVo getProductList(final Pageable pageable, final String search) {
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search);

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
                        .build();
    }
}
