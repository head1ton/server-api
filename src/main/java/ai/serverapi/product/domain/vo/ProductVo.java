package ai.serverapi.product.domain.vo;

import ai.serverapi.product.domain.entity.Product;
import ai.serverapi.product.domain.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductVo(
    Long id,
    String mainTitle,
    String mainExplanation,
    String productMainExplanation,
    String productSubExplanation,
    int originPrice,
    int price,
    String purchaseInquiry,
    String origin,
    String producer,
    String mainImage,
    String image1,
    String image2,
    String image3,
    Long viewCnt,
    Status status,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    SellerVo seller,
    CategoryVo category) {

    public ProductVo(final Product product) {
        this(
            product.getId(),
            product.getMainTitle(),
            product.getMainExplanation(),
            product.getProductMainExplanation(),
            product.getProductSubExplanation(),
            product.getOriginPrice(),
            product.getPrice(),
            product.getPurchaseInquiry(),
            product.getOrigin(),
            product.getProducer(),
            product.getMainImage(),
            product.getImage1(),
            product.getImage2(),
            product.getImage3(),
            product.getViewCnt(),
            product.getStatus(),
            product.getCreatedAt(),
            product.getModifiedAt(),
            new SellerVo(
                product.getSeller().getId(),
                product.getSeller().getEmail(),
                product.getSeller().getCompany(),
                product.getSeller().getAddress(),
                product.getSeller().getTel()),
            new CategoryVo(
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getCreatedAt(),
                product.getCategory().getModifiedAt())
        );
    }
}
