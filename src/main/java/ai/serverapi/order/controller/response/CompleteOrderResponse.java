package ai.serverapi.order.controller.response;

import ai.serverapi.order.domain.model.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class CompleteOrderResponse {

    private Long orderId;
    private String orderNumber;

    public static CompleteOrderResponse from(Order order) {
        return CompleteOrderResponse.builder()
                                    .orderId(order.getId())
                                    .orderNumber(order.getOrderNumber())
                                    .build();
    }
}
