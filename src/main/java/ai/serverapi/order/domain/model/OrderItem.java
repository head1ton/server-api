package ai.serverapi.order.domain.model;

import ai.serverapi.order.controller.response.OrderItemResponse;
import ai.serverapi.order.enums.OrderItemStatus;
import ai.serverapi.product.enums.ProductType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItem {

    private Long id;
    private Order order;
    private OrderProduct orderProduct;
    private OrderOption orderOption;
    private OrderItemStatus status;
    private int ea;
    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderItem create(Order order, OrderProduct orderProduct, OrderOption orderOption,
        int ea) {
        int price = orderProduct.getType() == ProductType.OPTION ? orderProduct.getPrice()
            + orderOption.getExtraPrice() : orderProduct.getPrice();
        int totalPrice = price * ea;

        return OrderItem.builder()
                        .order(order)
                        .orderProduct(orderProduct)
                        .orderOption(orderOption)
                        .status(OrderItemStatus.TEMP)
                        .ea(ea)
                        .productPrice(price)
                        .productTotalPrice(totalPrice)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build();
    }

    public OrderItemResponse toResponse() {
        return OrderItemResponse.builder()
                                .orderItemId(id)
                                .orderProduct(orderProduct.toResponse())
                                .orderOption(orderOption == null ? null : orderOption.toResponse())
                                .status(status)
                                .ea(ea)
                                .productPrice(productPrice)
                                .productTotalPrice(productTotalPrice)
                                .createdAt(createdAt)
                                .modifiedAt(modifiedAt)
                                .build();
    }
}