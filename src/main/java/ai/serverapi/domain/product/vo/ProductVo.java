package ai.serverapi.domain.product.vo;

import ai.serverapi.config.base.BaseVo;
import ai.serverapi.domain.product.entity.Product;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductVo extends BaseVo {

    private Long id;
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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private SellerVo seller;
    private CategoryVo category;

    public static ProductVo productReturnVo(final Product product) {
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
                        .viewCnt(product.getViewCnt())
                        .category(CategoryVo.builder()
                                            .categoryId(product.getCategory().getId())
                                            .name(product.getCategory().getName())
                                            .createdAt(product.getCategory().getCreatedAt())
                                            .modifiedAt(product.getCategory().getModifiedAt())
                                            .build())
                        .createdAt(product.getCreatedAt())
                        .modifiedAt(product.getModifiedAt())
                        .build();
    }

    public void putSeller(final SellerVo seller) {
        this.seller = seller;
    }
}
