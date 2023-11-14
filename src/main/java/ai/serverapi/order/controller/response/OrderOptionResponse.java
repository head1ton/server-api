package ai.serverapi.order.controller.response;

import ai.serverapi.product.enums.OptionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class OrderOptionResponse {

    private Long optionId;
    private String name;
    private int extraPrice;
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
