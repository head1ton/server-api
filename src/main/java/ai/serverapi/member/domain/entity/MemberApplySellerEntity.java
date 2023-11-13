package ai.serverapi.member.domain.entity;

import ai.serverapi.member.domain.model.MemberApplySeller;
import ai.serverapi.member.enums.MemberApplySellerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "member_apply_seller")
public class MemberApplySellerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;

    @NotNull
    private Long memberId;
    @Enumerated(EnumType.STRING)
    private MemberApplySellerStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public MemberApplySellerEntity(
        final Long memberId,
        final MemberApplySellerStatus status,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.memberId = memberId;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static MemberApplySellerEntity from(MemberApplySeller memberApplySeller) {
        MemberApplySellerEntity memberApplySellerEntity = new MemberApplySellerEntity();
        memberApplySellerEntity.id = memberApplySeller.getId();
        memberApplySellerEntity.memberId = memberApplySeller.getMemberId();
        memberApplySellerEntity.status = memberApplySeller.getStatus();
        memberApplySellerEntity.createdAt = memberApplySeller.getCreatedAt();
        memberApplySellerEntity.modifiedAt = memberApplySeller.getModifiedAt();
        return memberApplySellerEntity;
    }

    public static MemberApplySellerEntity of(final Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        return new MemberApplySellerEntity(memberId, MemberApplySellerStatus.APPLY, now, now);
    }

    public MemberApplySeller toModel() {
        return MemberApplySeller.builder()
                                .id(id)
                                .memberId(memberId)
                                .status(status)
                                .createdAt(createdAt)
                                .modifiedAt(modifiedAt)
                                .build();
    }

    public void patchApplyStatus(final MemberApplySellerStatus status) {
        this.status = status;
    }
}
