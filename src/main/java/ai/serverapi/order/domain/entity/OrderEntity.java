package ai.serverapi.order.domain.entity;

import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.domain.model.Member;
import ai.serverapi.order.domain.model.Order;
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
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Audited
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ORDERS")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private String orderNumber;

    @NotAudited
    @OneToMany(mappedBy = "order")
    private List<OrderItemEntity> orderItemList = new ArrayList<>();

    @NotAudited
    @OneToMany(mappedBy = "order")
    private List<DeliveryEntity> deliveryList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String orderName;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public OrderEntity(
        final MemberEntity member,
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

    public static OrderEntity from(Order order) {
        if (order == null) {
            return null;
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.id = order.getId();
        orderEntity.member = MemberEntity.from(
            Optional.ofNullable(
                        order.getMember())
                    .orElse(Member.builder().build()
                    )
        );
        orderEntity.orderNumber = order.getOrderNumber();
        orderEntity.orderItemList = Optional.ofNullable(order.getOrderItemList())
                                            .orElse(new ArrayList<>()).stream()
                                            .map(OrderItemEntity::from).collect(
                Collectors.toList());
        orderEntity.deliveryList = Optional.ofNullable(order.getDeliveryList())
                                           .orElse(new ArrayList<>()).stream()
                                           .map(DeliveryEntity::from).collect(
                Collectors.toList());
        orderEntity.status = order.getStatus();
        orderEntity.orderName = order.getOrderName();
        orderEntity.createdAt = order.getCreatedAt();
        orderEntity.modifiedAt = order.getModifiedAt();
        return orderEntity;
    }

    public static OrderEntity of(final MemberEntity member, final String orderName) {
        LocalDateTime now = LocalDateTime.now();
        return new OrderEntity(member, OrderStatus.TEMP, orderName, now, now);
    }

    public Order toModel() {
        return Order.builder()
                    .id(id)
                    .member(member.toModel())
                    .orderNumber(orderNumber)
                    .orderItemList(orderItemList.stream().map(OrderItemEntity::toModel)
                                                .collect(Collectors.toList()))
                    .deliveryList(deliveryList.stream().map(DeliveryEntity::toModel)
                                              .collect(Collectors.toList()))
                    .status(status)
                    .orderName(orderName)
                    .createdAt(createdAt)
                    .modifiedAt(modifiedAt)
                    .build();
    }

    public void statusComplete() {
        this.status = OrderStatus.COMPLETE;
        this.modifiedAt = LocalDateTime.now();
    }

    public void orderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void addOrderItem(OrderItemEntity orderItemEntity) {
        this.orderItemList.add(orderItemEntity);
    }
}
