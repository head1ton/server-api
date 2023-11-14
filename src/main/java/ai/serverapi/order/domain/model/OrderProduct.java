package ai.serverapi.order.domain.model;

import ai.serverapi.order.controller.response.OrderProductResponse;
import ai.serverapi.product.domain.model.Category;
import ai.serverapi.product.domain.model.Product;
import ai.serverapi.product.domain.model.Seller;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProduct {

    private Long id;
    private Long productId;
    private Seller seller;
    private Category category;
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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderProduct create(Product product) {
        return OrderProduct.builder()
                           .productId(product.getId())
                           .seller(product.getSeller())
                           .category(product.getCategory())
                           .mainTitle(product.getMainTitle())
                           .mainExplanation(product.getMainExplanation())
                           .productMainExplanation(product.getProductMainExplanation())
                           .productSubExplanation(product.getProductSubExplanation())
                           .originPrice(product.getOriginPrice())
                           .price(product.getPrice())
                           .purchaseInquiry(product.getPurchaseInquiry())
                           .origin(product.getOrigin())
                           .producer(product.getProducer())
                           .mainImage(product.getMainImage())
                           .image1(product.getImage1())
                           .image2(product.getImage2())
                           .image3(product.getImage3())
                           .viewCnt(product.getViewCnt())
                           .status(product.getStatus())
                           .type(product.getType())
                           .createdAt(product.getCreatedAt())
                           .modifiedAt(product.getModifiedAt())
                           .build();
    }

    public OrderProductResponse toResponse() {
        return OrderProductResponse.builder()
                                   .orderProductId(id)
                                   .seller(seller.toResponse())
                                   .category(category.toResponse())
                                   .mainTitle(mainTitle)
                                   .mainExplanation(mainExplanation)
                                   .productMainExplanation(productMainExplanation)
                                   .productSubExplanation(productSubExplanation)
                                   .originPrice(originPrice)
                                   .price(price)
                                   .purchaseInquiry(purchaseInquiry)
                                   .origin(origin)
                                   .producer(producer)
                                   .mainImage(mainImage)
                                   .image1(image1)
                                   .image2(image2)
                                   .image3(image3)
                                   .viewCnt(viewCnt)
                                   .status(status)
                                   .type(type)
                                   .createdAt(createdAt)
                                   .modifiedAt(modifiedAt)
                                   .build();
    }
}
