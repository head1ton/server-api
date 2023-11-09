package ai.serverapi.order.dto.response;

import ai.serverapi.order.domain.Order;
import ai.serverapi.order.domain.OrderItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TempOrderResponse {

    private Long orderId;
    private List<OrderItemVo> orderItemList;

    public static TempOrderResponse of(final Order order) {
        List<OrderItem> itemList = order.getOrderItemList();
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        for (OrderItem o : itemList) {
            orderItemVoList.add(OrderItemVo.of(o));
        }

        return new TempOrderResponse(order.getId(), orderItemVoList);
    }
}
