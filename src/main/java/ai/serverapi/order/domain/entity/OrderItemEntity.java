package ai.serverapi.order.domain.entity;

import ai.serverapi.order.domain.model.OrderItem;
import ai.serverapi.order.enums.OrderItemStatus;
import ai.serverapi.product.domain.entity.OptionEntity;
import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.enums.ProductType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
@Table(name = "order_item")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @NotAudited
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotAudited
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    @NotAudited
    private OptionEntity option;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    private int ea;

    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public OrderItemEntity(
        final OrderEntity order,
        final ProductEntity product,
        final OptionEntity option,
        final OrderItemStatus status,
        final int ea,
        final int productPrice,
        final int productTotalPrice,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.order = order;
        this.product = product;
        this.option = option;
        this.status = status;
        this.ea = ea;
        this.productPrice = productPrice;
        this.productTotalPrice = productTotalPrice;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrderItemEntity from(OrderItem orderItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.id = orderItem.getId();
        orderItemEntity.order = OrderEntity.from(orderItem.getOrder());
        orderItemEntity.product = ProductEntity.from(orderItem.getProduct());
        orderItemEntity.option = OptionEntity.from(orderItem.getOption());
        orderItemEntity.status = orderItem.getStatus();
        orderItemEntity.ea = orderItem.getEa();
        orderItemEntity.productPrice = orderItem.getProductPrice();
        orderItemEntity.productTotalPrice = orderItem.getProductTotalPrice();
        orderItemEntity.createdAt = orderItem.getCreatedAt();
        orderItemEntity.modifiedAt = orderItem.getModifiedAt();
        return orderItemEntity;
    }

    public static OrderItemEntity of(final OrderEntity order, final ProductEntity product,
        final OptionEntity option,
        final int ea) {
        LocalDateTime now = LocalDateTime.now();
        int price =
            product.getType() == ProductType.OPTION ? product.getPrice() + option.getExtraPrice()
                : product.getPrice();
        return new OrderItemEntity(order, product, option, OrderItemStatus.TEMP, ea, price,
            price * ea, now, now);
    }

    public OrderItem toModel() {
        return OrderItem.builder()
                        .id(id)
                        .order(order.toModel())
                        .product(product.toModel())
                        .option(option.toModel())
                        .status(status)
                        .ea(ea)
                        .productPrice(productPrice)
                        .productTotalPrice(productTotalPrice)
                        .createdAt(createdAt)
                        .modifiedAt(modifiedAt)
                        .build();
    }

    public void statusComplete() {
        this.status = OrderItemStatus.ORDER;
        this.modifiedAt = LocalDateTime.now();
    }
}
