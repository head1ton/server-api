package ai.serverapi.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull(message = "company 필수입니다.")
    private String company;
    @NotNull(message = "tel 필수입니다.")
    @Column(length = 11)
    private String tel;
    @NotNull(message = "address 필수입니다.")
    private String address;
    @Email(message = "email 형식을 맞춰주세요.")
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Seller(
        final Member member,
        final String company,
        final String tel,
        final String address,
        final String email,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.member = member;
        this.company = company;
        this.tel = tel;
        this.address = address;
        this.email = email;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Seller of(Member member, String company, String tel, String address,
        String email) {
        LocalDateTime now = LocalDateTime.now();
        tel = tel.replaceAll("-", "");
        return new Seller(member, company, tel, address, email, now, now);
    }
}
