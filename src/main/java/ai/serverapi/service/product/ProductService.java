package ai.serverapi.service.product;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.product.Product;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.repository.member.MemberRepository;
import ai.serverapi.repository.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                        .build();
    }
}
