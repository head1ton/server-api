package ai.serverapi.order.dto.response;

import ai.serverapi.order.domain.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class OrderVo {

    private final Long orderId;
    private final String orderNumber;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final MemberResponse member;
    private final List<OrderItemVo> orderItemList;
    private final DeliveryResponse delivery;

    public OrderVo(Order order) {
        this.orderId = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.createdAt = order.getCreatedAt();
        this.modifiedAt = order.getModifiedAt();
        this.member = ai.serverapi.order.dto.response.MemberResponse.fromMemberEntity(
            order.getMember());
        this.orderItemList = order.getOrderItemList().stream()
                                  .map(OrderItemVo::from)
                                  .toList();
        this.delivery =
            order.getDeliveryList().stream().map(DeliveryResponse::fromDeliveryEntity).toList()
                 .size() > 0 ? order.getDeliveryList().stream()
                                    .map(DeliveryResponse::fromDeliveryEntity).toList().get(0)
                : null;
    }
}
