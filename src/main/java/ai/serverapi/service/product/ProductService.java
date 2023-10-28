package ai.serverapi.service.product;

import ai.serverapi.common.s3.S3Service;
import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.product.Product;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.repository.member.MemberRepository;
import ai.serverapi.repository.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final Environment env;
    private final S3Service s3Service;

    public ProductVo postProduct(
        final ProductDto productDto,
        final List<MultipartFile> files,
        final HttpServletRequest request) {

        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));

        String s3Url = env.getProperty("cloud.s3.url");
        List<String> putFileUrlList = getFileUrlList(files, memberId);

        productDto.updateImages(putFileUrlList);

        Product product = productRepository.save(Product.of(member, productDto));
        ProductVo productVo = ProductVo.builder()
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
        productVo.updateImages(s3Url, putFileUrlList);

        return productVo;
    }

    private List<String> getFileUrlList(final List<MultipartFile> files, final Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter pathFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String path = now.format(pathFormatter);
        String fileName = now.format(timeFormatter);

        return s3Service.putObject(String.format("product/%s/%s", memberId, path),
            fileName, files);
    }
}
