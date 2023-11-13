package ai.serverapi.product.controller.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PutProductRequest {

    @JsonProperty("product_id")
    @NotNull(message = "id  비어있을 수 없습니다.")
    private Long productId;
    @NotNull(message = "category_id 비어있을 수 없습니다.")
    @JsonProperty("category_id")
    private Long categoryId;
    @NotNull(message = "main_title 비어있을 수 없습니다.")
    @JsonProperty("main_title")
    private String mainTitle;
    @NotNull(message = "main_explanation 비어있을 수 없습니다.")
    @JsonProperty("main_explanation")
    private String mainExplanation;
    @NotNull(message = "product_main_explanation 비어있을 수 없습니다.")
    @JsonProperty("product_main_explanation")
    private String productMainExplanation;
    @NotNull(message = "product_sub_explanation 비어있을 수 없습니다.")
    @JsonProperty("product_sub_explanation")
    private String productSubExplanation;
    @NotNull(message = "origin_price 비어있을 수 없습니다.")
    @JsonProperty("origin_price")
    private int originPrice;
    @NotNull(message = "price 비어있을 수 없습니다.")
    private int price;
    @NotNull(message = "purchase_inquiry 비어있을 수 없습니다.")
    @JsonProperty("purchase_inquiry")
    private String purchaseInquiry;
    @NotNull(message = "origin 비어있을 수 없습니다.")
    private String origin;
    @NotNull(message = "producer 비어있을 수 없습니다.")
    private String producer;
    @NotNull(message = "main_image 비어있을 수 없습니다.")
    @JsonProperty("main_image")
    private String mainImage;
    private String image1;
    private String image2;
    private String image3;
    @NotNull(message = "status 비어있을 수 없습니다.")
    private String status;
    private String type;
    private int ea;
    @JsonProperty("option_list")
    private List<OptionRequest> optionList;
}
