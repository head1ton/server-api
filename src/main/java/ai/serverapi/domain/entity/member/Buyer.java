package ai.serverapi.domain.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyer_id")
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    @Column(length = 11)
    private String tel;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Buyer(final Long id, final String name, final String email, final String tel,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Buyer of(final Long id, final String name, final String email,
        final String tel) {
        LocalDateTime now = LocalDateTime.now();
        String telNumber = tel.replace("-", "");
        return new Buyer(id, name, email, telNumber, now, now);
    }

    public static Buyer ofEmpty() {
        return new Buyer(null, "", "", "", null, null);
    }

    public void put(final @NotNull String name, final @NotNull String email,
        final @NotNull String tel) {
        String telNum = tel.replaceAll("-", "");
        this.name = name;
        this.email = email;
        this.tel = telNum;
        this.modifiedAt = LocalDateTime.now();
    }
}
