package ai.serverapi.product.controller.response;

import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.enums.OptionStatus;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(Include.NON_NULL)
@JsonNaming(SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public class ProductResponse {

    private Long productId;
    private String mainTitle;
    private String mainExplanation;
    private String productMainExplanation;
    private String productSubExplanation;
    private int originPrice;
    private int price;
    private String purchaseInquiry;
    private String origin;
    private String producer;
    private String mainImage;
    private String image1;
    private String image2;
    private String image3;
    private Long viewCnt;
    private ProductStatus status;
    private ProductType type;
    private int ea;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private SellerResponse seller;
    private CategoryResponse category;
    private List<OptionVo> optionList;

    public static ProductResponse from(ProductEntity productEntity) {
        return ProductResponse.builder()
                              .productId(productEntity.getId())
                              .mainTitle(productEntity.getMainTitle())
                              .mainExplanation(productEntity.getMainExplanation())
                              .productMainExplanation(productEntity.getProductMainExplanation())
                              .productSubExplanation(productEntity.getProductSubExplanation())
                              .originPrice(productEntity.getOriginPrice())
                              .price(productEntity.getPrice())
                              .purchaseInquiry(productEntity.getPurchaseInquiry())
                              .origin(productEntity.getOrigin())
                              .producer(productEntity.getProducer())
                              .mainImage(productEntity.getMainImage())
                              .image1(productEntity.getImage1())
                              .image2(productEntity.getImage2())
                              .image3(productEntity.getImage3())
                              .viewCnt(productEntity.getViewCnt())
                              .status(productEntity.getStatus())
                              .type(productEntity.getType())
                              .ea(productEntity.getEa())
                              .createdAt(productEntity.getCreatedAt())
                              .modifiedAt(productEntity.getModifiedAt())
                              .seller(SellerResponse.from(productEntity.getSeller()))
                              .category(CategoryResponse.from(productEntity))
                              .optionList(OptionVo.getVoList(productEntity.getOptionList().stream()
                                                                          .filter(o -> o.getStatus()
                                                                                  .equals(
                                                                                      OptionStatus.NORMAL))
                                                                          .toList()))
                              .build();
    }
}
