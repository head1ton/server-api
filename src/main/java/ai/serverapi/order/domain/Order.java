package ai.serverapi.order.domain;

import ai.serverapi.member.domain.Member;
import ai.serverapi.order.enums.OrdersStatus;
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

    @Enumerated(EnumType.STRING)
    private OrdersStatus status;

    private String ordererName;
    private String ordererAddress;
    private String ordererZonecode;
    private String ordererTel;
    private String recipientName;
    private String recipientAddress;
    private String recipientZonecode;
    private String recipientTel;
    private int orderTotalPrice;
    private String etc;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Order(
        final Member member,
        final OrdersStatus status,
        final String ordererName,
        final String ordererAddress,
        final String ordererZonecode,
        final String ordererTel,
        final String recipientName,
        final String recipientAddress,
        final String recipientZonecode,
        final String recipientTel,
        final int orderTotalPrice,
        final String etc,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.member = member;
        this.status = status;
        this.ordererName = ordererName;
        this.ordererAddress = ordererAddress;
        this.ordererZonecode = ordererZonecode;
        this.ordererTel = ordererTel;
        this.recipientName = recipientName;
        this.recipientAddress = recipientAddress;
        this.recipientZonecode = recipientZonecode;
        this.recipientTel = recipientTel;
        this.orderTotalPrice = orderTotalPrice;
        this.etc = etc;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Order of(final Member member) {
        LocalDateTime now = LocalDateTime.now();
        return new Order(
            member,
            OrdersStatus.TEMP,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            0,
            "",
            now,
            now);
    }
}
