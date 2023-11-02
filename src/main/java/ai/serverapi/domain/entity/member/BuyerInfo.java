package ai.serverapi.domain.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BuyerInfo {

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
}
