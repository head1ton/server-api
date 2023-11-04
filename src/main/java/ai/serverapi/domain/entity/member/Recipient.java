package ai.serverapi.domain.entity.member;

import ai.serverapi.domain.enums.member.RecipientInfoStatus;
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
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotNull
    private String tel;

    @Enumerated(EnumType.STRING)
    private RecipientInfoStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Recipient(
        final Member member,
        final String name,
        final String address,
        final String tel,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.member = member;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Recipient of(
        final Member member,
        final String name,
        final @NotNull(message = "address 필수입니다.") String address,
        final @NotNull(message = "tel 필수입니다.") String tel,
        final RecipientInfoStatus status) {
        String telNum = tel.replaceAll("-", "");
        LocalDateTime now = LocalDateTime.now();
        return new Recipient(member, name, address, telNum, now, now);
    }
}
