package ai.serverapi.order.domain.vo;

import ai.serverapi.order.domain.entity.OrderEntity;
import ai.serverapi.order.dto.response.DeliveryResponse;
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
    private final MemberVo member;
    private final List<OrderItemVo> orderItemList;
    private final DeliveryResponse delivery;

    public OrderVo(OrderEntity orderEntity) {
        this.orderId = orderEntity.getId();
        this.orderNumber = orderEntity.getOrderNumber();
        this.createdAt = orderEntity.getCreatedAt();
        this.modifiedAt = orderEntity.getModifiedAt();
        this.member = MemberVo.fromMemberEntity(
            orderEntity.getMember());
        this.orderItemList = orderEntity.getOrderItemList().stream()
                                        .map(OrderItemVo::from)
                                        .toList();
        this.delivery =
            orderEntity.getDeliveryList().stream().map(DeliveryResponse::fromDeliveryEntity)
                       .toList()
                       .size() > 0 ? orderEntity.getDeliveryList().stream()
                                                .map(DeliveryResponse::fromDeliveryEntity).toList()
                                                .get(0)
                : null;
    }
}
