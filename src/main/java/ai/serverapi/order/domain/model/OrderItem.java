package ai.serverapi.order.domain.model;

import ai.serverapi.order.enums.OrderItemStatus;
import ai.serverapi.product.domain.model.Option;
import ai.serverapi.product.domain.model.Product;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItem {

    private Long id;
    private Order order;
    // TODO OrderItem에서 product, option 자체를 분리해야할지 고민해봐야 함
    private Product product;
    private Option option;
    private OrderItemStatus status;
    private int ea;
    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}