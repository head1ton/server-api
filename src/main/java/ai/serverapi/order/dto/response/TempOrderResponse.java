package ai.serverapi.order.dto.response;

import ai.serverapi.order.domain.entity.OrderEntity;
import ai.serverapi.order.domain.entity.OrderItemEntity;
import ai.serverapi.order.domain.vo.OrderItemVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TempOrderResponse {

    private Long orderId;
    private List<OrderItemVo> orderItemList;

    public static TempOrderResponse from(final OrderEntity orderEntity) {
        List<OrderItemEntity> itemList = orderEntity.getOrderItemList();
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        for (OrderItemEntity o : itemList) {
            orderItemVoList.add(OrderItemVo.from(o));
        }

        return TempOrderResponse.builder()
                                .orderId(orderEntity.getId())
                                .orderItemList(orderItemVoList)
                                .build();
    }
}
