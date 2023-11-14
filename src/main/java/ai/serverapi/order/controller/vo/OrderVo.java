package ai.serverapi.order.controller.vo;

import ai.serverapi.order.domain.model.OrderItem;
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

    private Long orderId;
    private String orderNumber;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private MemberVo member;
    private List<OrderItem> orderItemList;
    private DeliveryVo delivery;

}
