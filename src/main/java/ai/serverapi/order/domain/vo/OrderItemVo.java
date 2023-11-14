package ai.serverapi.order.domain.vo;

import ai.serverapi.order.domain.entity.OrderItemEntity;
import ai.serverapi.order.dto.response.CategoryResponse;
import ai.serverapi.product.controller.response.OptionVo;
import ai.serverapi.product.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
public class OrderItemVo {

    private Long productId;
    private int ea;
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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private SellerVo seller;
    private CategoryResponse category;
    private OptionVo option;

    public static OrderItemVo from(OrderItemEntity orderItemEntity) {
        return new OrderItemVo(orderItemEntity.getProduct().getId(), orderItemEntity.getEa(),
            orderItemEntity.getProduct().getMainTitle(),
            orderItemEntity.getProduct().getMainExplanation(),
            orderItemEntity.getProduct().getProductMainExplanation(),
            orderItemEntity.getProduct().getProductSubExplanation(),
            orderItemEntity.getProduct().getOriginPrice(), orderItemEntity.getProduct().getPrice(),
            orderItemEntity.getProduct().getPurchaseInquiry(),
            orderItemEntity.getProduct().getOrigin(), orderItemEntity.getProduct().getProducer(),
            orderItemEntity.getProduct().getMainImage(), orderItemEntity.getProduct().getImage1(),
            orderItemEntity.getProduct().getImage2(), orderItemEntity.getProduct().getImage3(),
            orderItemEntity.getProduct().getViewCnt(), orderItemEntity.getProduct().getStatus(),
            orderItemEntity.getProduct().getCreatedAt(), orderItemEntity.getModifiedAt(),
            SellerVo.of(orderItemEntity.getProduct().getSeller()),
            CategoryResponse.of(orderItemEntity.getProduct().getCategory()),
            OptionVo.of(orderItemEntity.getOption()));
    }
}
