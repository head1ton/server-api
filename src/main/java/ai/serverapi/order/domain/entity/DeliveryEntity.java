package ai.serverapi.order.domain.entity;

import ai.serverapi.order.domain.model.Delivery;
import ai.serverapi.order.enums.DeliveryStatus;
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

@Audited
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery")
public class DeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItemEntity orderItem;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private String ownerName;
    private String ownerZonecode;
    private String ownerAddress;
    private String ownerAddressDetail;
    private String ownerTel;

    private String recipientName;
    private String recipientZonecode;
    private String recipientAddress;
    private String recipientAddressDetail;
    private String recipientTel;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static DeliveryEntity from(Delivery delivery) {
        DeliveryEntity deliveryEntity = new DeliveryEntity();
        deliveryEntity.id = delivery.getId();
        deliveryEntity.order = OrderEntity.from(delivery.getOrder());
        deliveryEntity.orderItem = OrderItemEntity.from(delivery.getOrderItem());
        deliveryEntity.status = delivery.getStatus();
        deliveryEntity.ownerName = delivery.getOwnerName();
        deliveryEntity.ownerZonecode = delivery.getOwnerZonecode();
        deliveryEntity.ownerAddress = delivery.getOwnerAddress();
        deliveryEntity.ownerAddressDetail = delivery.getOwnerAddressDetail();
        deliveryEntity.ownerTel = delivery.getOwnerTel();
        deliveryEntity.recipientName = delivery.getRecipientName();
        deliveryEntity.recipientZonecode = delivery.getRecipientZonecode();
        deliveryEntity.recipientAddress = delivery.getRecipientAddress();
        deliveryEntity.recipientAddressDetail = delivery.getRecipientAddressDetail();
        deliveryEntity.recipientTel = delivery.getRecipientTel();
        deliveryEntity.createdAt = delivery.getCreatedAt();
        deliveryEntity.modifiedAt = delivery.getModifiedAt();
        return deliveryEntity;
    }

    public Delivery toModel() {
        return Delivery.builder()
                       .id(id)
                       .order(order.toModel())
                       .orderItem(orderItem.toModel())
                       .status(status)
                       .ownerName(ownerName)
                       .ownerZonecode(ownerZonecode)
                       .ownerAddress(ownerAddress)
                       .ownerAddressDetail(ownerAddressDetail)
                       .ownerTel(ownerTel)
                       .recipientName(recipientName)
                       .recipientZonecode(recipientZonecode)
                       .recipientAddress(recipientAddress)
                       .recipientAddressDetail(recipientAddressDetail)
                       .recipientTel(recipientTel)
                       .createdAt(createdAt)
                       .modifiedAt(modifiedAt)
                       .build();
    }
}
