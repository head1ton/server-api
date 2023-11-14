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
                                .productId(orderProduct.getProductId())
                                .status(orderProduct.getStatus())
                                .ea(ea)
                                .productPrice(productPrice)
                                .productTotalPrice(productTotalPrice)
                                .createdAt(createdAt)
                                .modifiedAt(modifiedAt)
                                .option(orderOption == null ? null : orderOption.toResponse())
                                .seller(orderProduct.getSeller().toResponse())
                                .category(orderProduct.getCategory().toResponse())
                                .mainTitle(orderProduct.getMainTitle())
                                .mainExplanation(orderProduct.getMainExplanation())
                                .productMainExplanation(orderProduct.getProductMainExplanation())
                                .productSubExplanation(orderProduct.getProductSubExplanation())
                                .originPrice(orderProduct.getOriginPrice())
                                .price(orderProduct.getPrice())
                                .purchaseInquiry(orderProduct.getPurchaseInquiry())
                                .origin(orderProduct.getOrigin())
                                .producer(orderProduct.getProducer())
                                .mainImage(orderProduct.getMainImage())
                                .image1(orderProduct.getImage1())
                                .image2(orderProduct.getImage2())
                                .image3(orderProduct.getImage3())
                                .viewCnt(orderProduct.getViewCnt())
                                .type(orderProduct.getType())
                                .build();
    }
}