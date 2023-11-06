package ai.serverapi.member.domain.entity;

import ai.serverapi.member.domain.enums.IntroduceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Introduce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introduce_id")
    private Long id;

    private String url;

    @Enumerated(EnumType.STRING)
    private IntroduceStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Introduce(
        final Seller seller,
        final String url,
        final IntroduceStatus status,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.seller = seller;
        this.url = url;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Introduce of(
        final Seller seller,
        final String url,
        final IntroduceStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new Introduce(seller, url, status, now, now);
    }
}
