package ai.serverapi.service.product;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.product.Product;
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

    public Long postProduct(final HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));

        Product product = productRepository.save(
            Product.of(member, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명"));

        return product.getId();
    }
}
