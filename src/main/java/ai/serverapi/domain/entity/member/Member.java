package ai.serverapi.domain.entity.member;

import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.enums.Role;
import ai.serverapi.domain.enums.member.SnsJoinType;
import ai.serverapi.domain.enums.member.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
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

    @Enumerated(EnumType.STRING)
    private Status status;

    private String snsId;
    @Enumerated(EnumType.STRING)
    private SnsJoinType snsType;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Builder
    public Member(
        final String email,
        final String password,
        final String nickname,
        final String name,
        final String birth,
        final Role role,
        final String snsId,
        final SnsJoinType snsType,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birth = birth;
        this.role = role;
        this.snsId = snsId;
        this.snsType = snsType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public Member(
        final Long id,
        final String email,
        final String password,
        final String nickname,
        final String name,
        final String birth,
        final Role role,
        final String snsId,
        final SnsJoinType snsType,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birth = birth;
        this.role = role;
        this.snsId = snsId;
        this.snsType = snsType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Member of(final JoinDto joinDto) {
        LocalDateTime now = LocalDateTime.now();
        return new Member(joinDto.getEmail(),
            joinDto.getPassword(),
            joinDto.getNickname(),
            joinDto.getName(),
            joinDto.getBirth(),
            Role.MEMBER,
            null,
            null,
            now,
            now);
    }

    public static Member of(final JoinDto joinDto, String snsId, SnsJoinType snsType) {
        LocalDateTime now = LocalDateTime.now();
        return new Member(joinDto.getEmail(),
            joinDto.getPassword(),
            joinDto.getNickname(),
            joinDto.getName(),
            joinDto.getBirth(),
            Role.MEMBER,
            snsId,
            snsType,
            now,
            now);
    }

    public void patchMemberRole(final Role role) {
        this.role = role;
    }
}
