package ai.serverapi.domain.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDto {

    @NotNull
    @JsonProperty("main_title")
    private String mainTitle;
    @NotNull
    @JsonProperty("main_explanation")
    private String mainExplanation;
    @NotNull
    @JsonProperty("product_main_explanation")
    private String productMainExplanation;
    @NotNull
    @JsonProperty("product_sub_explanantion")
    private String productSubExplanation;
    @NotNull
    @JsonProperty("origin_price")
    private int originPrice;
    @NotNull
    private int price;
    @NotNull
    @JsonProperty("purchase_inquiry")
    private String purchaseInquiry;
    @NotNull
    private String origin;
    @NotNull
    private String producer;
    @NotNull
    @JsonProperty("main_image")
    private String mainImage;
    private String image1;
    private String image2;
    private String image3;
}
