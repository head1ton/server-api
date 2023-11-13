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

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class ProductRequest {

    @NotNull(message = "category_id 필수입니다.")
    @JsonProperty("category_id")
    private Long categoryId;
    @NotNull(message = "main_title 필수입니다.")
    @JsonProperty("main_title")
    private String mainTitle;
    @NotNull(message = "main_description 필수입니다.")
    @JsonProperty("main_explanation")
    private String mainExplanation;
    @NotNull(message = "product_main_explanation 필수입니다.")
    @JsonProperty("product_main_explanation")
    private String productMainExplanation;
    @NotNull(message = "product_sub_explanation 필수입니다.")
    @JsonProperty("product_sub_explanation")
    private String productSubExplanation;
    @NotNull(message = "origin_price 필수입니다.")
    @JsonProperty("origin_price")
    private int originPrice;
    @NotNull(message = "price 필수입니다.")
    private int price;
    @NotNull(message = "purchase_inquiry 필수입니다.")
    @JsonProperty("purchase_inquiry")
    private String purchaseInquiry;
    @NotNull(message = "origin 필수입니다.")
    private String origin;
    @NotNull(message = "producer 필수입니다.")
    private String producer;
    @NotNull(message = "main_image 필수입니다.")
    @JsonProperty("main_image")
    private String mainImage;
    private String image1;
    private String image2;
    private String image3;
    @NotNull(message = "status 필수입니다.")
    private String status;
    private int ea;
    @JsonProperty("option_list")
    private List<OptionRequest> optionList;
    @NotNull(message = "type 필수입니다.")
    private String type;
}
