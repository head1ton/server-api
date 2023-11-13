package ai.serverapi.order.domain;

import ai.serverapi.order.enums.OrderItemStatus;
import ai.serverapi.product.domain.Option;
import ai.serverapi.product.domain.Product;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Getter
@NoArgsConstructor
@Audited
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @NotAudited
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotAudited
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    @NotAudited
    private Option option;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    private int ea;

    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public OrderItem(
        final Order order,
        final Product product,
        final Option option,
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

    public static OrderItem of(final Order order, final Product product, final Option option,
        final int ea) {
        LocalDateTime now = LocalDateTime.now();
        int price =
            product.getType() == ProductType.OPTION ? product.getPrice() + option.getExtraPrice()
                : product.getPrice();
        return new OrderItem(order, product, option, OrderItemStatus.TEMP, ea, price,
            price * ea, now, now);
    }

    public void statusComplete() {
        this.status = OrderItemStatus.ORDER;
        this.modifiedAt = LocalDateTime.now();
    }
}
