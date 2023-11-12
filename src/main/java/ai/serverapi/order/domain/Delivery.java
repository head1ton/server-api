package ai.serverapi.order.domain;

import ai.serverapi.order.dto.request.CompleteOrderRequest;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

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

    public Delivery(final Order order, final OrderItem orderItem, final DeliveryStatus status,
        final String ownerName,
        final String ownerZonecode, final String ownerAddress, final String ownerAddressDetail,
        final String ownerTel,
        final String recipientName, final String recipientZonecode, final String recipientAddress,
        final String recipientAddressDetail, final String recipientTel,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.order = order;
        this.orderItem = orderItem;
        this.status = status;
        this.ownerName = ownerName;
        this.ownerZonecode = ownerZonecode;
        this.ownerAddress = ownerAddress;
        this.ownerAddressDetail = ownerAddressDetail;
        this.ownerTel = ownerTel;
        this.recipientName = recipientName;
        this.recipientZonecode = recipientZonecode;
        this.recipientAddress = recipientAddress;
        this.recipientAddressDetail = recipientAddressDetail;
        this.recipientTel = recipientTel;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Delivery of(
        final Order order,
        final OrderItem oi,
        final CompleteOrderRequest completeOrderRequest) {
        LocalDateTime now = LocalDateTime.now();
        return new Delivery(
            order,
            oi,
            DeliveryStatus.TEMP,
            completeOrderRequest.getOwnerName(),
            completeOrderRequest.getOwnerZonecode(),
            completeOrderRequest.getOwnerAddress(),
            completeOrderRequest.getOwnerAddressDetail(),
            completeOrderRequest.getOwnerTel(),
            completeOrderRequest.getRecipientName(),
            completeOrderRequest.getRecipientZonecode(),
            completeOrderRequest.getRecipientAddress(),
            completeOrderRequest.getRecipientAddressDetail(),
            completeOrderRequest.getRecipientTel(),
            now, now
        );
    }
}
