package ai.serverapi.member.domain.entity;

import ai.serverapi.member.enums.RecipientInfoStatus;
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
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "recipient")
public class RecipientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotNull
    private String addressDetails;
    @NotNull
    private String zonecode;
    @NotNull
    private String tel;

    @Enumerated(EnumType.STRING)
    private RecipientInfoStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public RecipientEntity(
        final MemberEntity member,
        final String name,
        final String zonecode,
        final String address,
        final String addressDetails,
        final String tel,
        final RecipientInfoStatus status,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.member = member;
        this.name = name;
        this.zonecode = zonecode;
        this.address = address;
        this.addressDetails = addressDetails;
        this.tel = tel;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static RecipientEntity of(
        final MemberEntity member,
        final String name,
        final @NotNull(message = "zonecode 필수입니다.") String zonecode,
        final @NotNull(message = "address 필수입니다.") String address,
        final @NotNull(message = "addressDetails 필수입니다.") String addressDetails,
        final @NotNull(message = "tel 필수입니다.") String tel,
        final RecipientInfoStatus status) {
        String telNum = tel.replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        return new RecipientEntity(member, name, zonecode, address, addressDetails, telNum, status,
            now,
            now);
    }
}
