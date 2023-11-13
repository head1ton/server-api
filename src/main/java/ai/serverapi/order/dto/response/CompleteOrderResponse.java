package ai.serverapi.order.dto.response;

import ai.serverapi.order.domain.entity.OrderEntity;
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

    public static CompleteOrderResponse from(OrderEntity orderEntity) {
        return CompleteOrderResponse.builder()
                                    .orderId(orderEntity.getId())
                                    .orderNumber(orderEntity.getOrderNumber())
                                    .build();
    }
}
