package ai.serverapi.order.controller.response;

import ai.serverapi.order.enums.OrderItemStatus;
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

    private Long orderItemId;
    private OrderProductResponse orderProduct;
    private OrderOptionResponse orderOption;
    private OrderItemStatus status;
    private int ea;
    private int productPrice;
    private int productTotalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
