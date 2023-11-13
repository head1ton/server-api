package ai.serverapi.order.domain.model;

import ai.serverapi.member.domain.model.Member;
import ai.serverapi.order.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Order {

    private Long id;
    private Member member;
    private String orderNumber;
    private List<OrderItem> orderItemList;
    private List<Delivery> deliveryList;
    private OrderStatus status;
    private String orderName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
