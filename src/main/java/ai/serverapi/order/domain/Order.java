package ai.serverapi.order.domain;

import ai.serverapi.member.domain.Member;
import ai.serverapi.order.enums.OrderStatus;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Audited
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String orderNumber;

    @NotAudited
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @NotAudited
    @OneToMany(mappedBy = "order")
    private List<Delivery> deliveryList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String orderName;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Order(
        final Member member,
        final OrderStatus status,
        final String orderName,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.member = member;
        this.status = status;
        this.orderName = orderName;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }


    public static Order of(final Member member, final String orderName) {
        LocalDateTime now = LocalDateTime.now();
        return new Order(member, OrderStatus.TEMP, orderName, now, now);
    }

    public void statusComplete() {
        this.status = OrderStatus.COMPLETE;
        this.modifiedAt = LocalDateTime.now();
    }

    public void orderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
