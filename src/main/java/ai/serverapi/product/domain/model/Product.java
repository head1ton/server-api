package ai.serverapi.product.domain.model;

import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Product {

    private final Long id;
    private final Seller seller;
    private final Category category;
    private final String mainTitle;
    private final String mainExplanation;
    private final String productMainExplanation;
    private final String productSubExplanation;
    private final int originPrice;
    private final int price;
    private final String purchaseInquiry;
    private final String origin;
    private final String producer;
    private final String mainImage;
    private final String image1;
    private final String image2;
    private final String image3;
    private final Long viewCnt;
    private final ProductStatus status;
    private final int ea;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private List<Option> optionList = new ArrayList<>();
    private final ProductType type;

    @Builder
    public Product(Long id, Seller seller, Category category, String mainTitle,
        String mainExplanation,
        String productMainExplanation, String productSubExplanation, int originPrice, int price,
        String purchaseInquiry, String origin, String producer, String mainImage, String image1,
        String image2, String image3, Long viewCnt, ProductStatus status, int ea,
        LocalDateTime createdAt, LocalDateTime modifiedAt, List<Option> optionList,
        ProductType type) {
        this.id = id;
        this.seller = seller;
        this.category = category;
        this.mainTitle = mainTitle;
        this.mainExplanation = mainExplanation;
        this.productMainExplanation = productMainExplanation;
        this.productSubExplanation = productSubExplanation;
        this.originPrice = originPrice;
        this.price = price;
        this.purchaseInquiry = purchaseInquiry;
        this.origin = origin;
        this.producer = producer;
        this.mainImage = mainImage;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.viewCnt = viewCnt;
        this.status = status;
        this.ea = ea;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.optionList = optionList;
        this.type = type;
    }
}
