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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Audited
@Entity
@Getter
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String orderName;
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
}
