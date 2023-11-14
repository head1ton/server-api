package ai.serverapi.order.controller.response;

import ai.serverapi.order.domain.model.Order;
import ai.serverapi.order.domain.model.OrderItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderInfoResponse {

    private Long orderId;
    private List<OrderItemResponse> orderItemList;

    public static OrderInfoResponse create(Order order) {
        return OrderInfoResponse.builder()
                                .orderId(order.getId())
                                .orderItemList(order.getOrderItemList().stream().map(
                                    OrderItem::toResponse).toList())
                                .build();
    }

}
