package ai.serverapi.order.controller.response;

import ai.serverapi.product.controller.response.CategoryResponse;
import ai.serverapi.product.controller.response.SellerResponse;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class OrderItemResponse {

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
    private OrderOptionResponse option;
    private SellerResponse seller;
    private CategoryResponse category;
    private int ea;
    private int productPrice;
    private int productTotalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
