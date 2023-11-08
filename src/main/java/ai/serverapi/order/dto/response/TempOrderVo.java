package ai.serverapi.order.dto.response;

import ai.serverapi.order.domain.OrdersDetail;
import ai.serverapi.product.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TempOrderVo {

    private final Long productId;
    private final int ea;
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
    private final Status status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final SellerResponse seller;
    private final CategoryResponse category;

    private TempOrderVo(Long productId, int ea, String mainTitle, String mainExplanation,
        String productMainExplanation, String productSubExplanation, int originPrice, int price,
        String purchaseInquiry, String origin, String producer, String mainImage, String image1,
        String image2, String image3, Long viewCnt, Status status, LocalDateTime createdAt,
        LocalDateTime modifiedAt, SellerResponse seller, CategoryResponse category) {
        this.productId = productId;
        this.ea = ea;
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
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.seller = seller;
        this.category = category;
    }

    public static TempOrderVo of(OrdersDetail ordersDetail) {
        return new TempOrderVo(ordersDetail.getProduct().getId(), ordersDetail.getEa(),
            ordersDetail.getProduct().getMainTitle(),
            ordersDetail.getProduct().getMainExplanation(),
            ordersDetail.getProduct().getProductMainExplanation(),
            ordersDetail.getProduct().getProductSubExplanation(),
            ordersDetail.getProduct().getOriginPrice(), ordersDetail.getProduct().getPrice(),
            ordersDetail.getProduct().getPurchaseInquiry(),
            ordersDetail.getProduct().getOrigin(), ordersDetail.getProduct().getProducer(),
            ordersDetail.getProduct().getMainImage(), ordersDetail.getProduct().getImage1(),
            ordersDetail.getProduct().getImage2(), ordersDetail.getProduct().getImage3(),
            ordersDetail.getProduct().getViewCnt(), ordersDetail.getProduct().getStatus(),
            ordersDetail.getProduct().getCreatedAt(), ordersDetail.getModifiedAt(),
            SellerResponse.of(ordersDetail.getProduct().getSeller()),
            CategoryResponse.of(ordersDetail.getProduct().getCategory()));
    }
}
