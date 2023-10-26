package ai.serverapi.domain.entity.member;

import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    @NotNull
    private Long id;

    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String nickname;
    @NotNull
    private String name;
    private String birth;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String snsId;
    private String snsType;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime modifiedAt = LocalDateTime.now();

    @Builder
    public Member(
        final String email,
        final String password,
        final String nickname,
        final String name,
        final String birth,
        final Role role,
        final String snsId,
        final String snsType) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birth = birth;
        this.role = role;
        this.snsId = snsId;
        this.snsType = snsType;
    }

    public static Member createMember(final JoinDto joinDto) {
        return new Member(joinDto.getEmail(),
            joinDto.getPassword(),
            joinDto.getNickname(),
            joinDto.getName(),
            joinDto.getBirth(),
            Role.USER,
            null, null);
    }
}
