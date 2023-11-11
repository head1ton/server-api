package ai.serverapi.member.domain;

import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.member.enums.Role;
import ai.serverapi.member.enums.SnsJoinType;
import ai.serverapi.member.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private final List<Recipient> recipientList = new LinkedList<>();

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
        final Status status,
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
        this.status = status;
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

    public static Member of(final JoinRequest joinRequest) {
        LocalDateTime now = LocalDateTime.now();
        return new Member(joinRequest.getEmail(),
            joinRequest.getPassword(),
            joinRequest.getNickname(),
            joinRequest.getName(),
            joinRequest.getBirth(),
            Role.MEMBER,
            null,
            null,
            Status.NORMAL,
            now,
            now);
    }

    public static Member of(final JoinRequest joinRequest, String snsId, SnsJoinType snsType) {
        LocalDateTime now = LocalDateTime.now();
        return new Member(joinRequest.getEmail(),
            joinRequest.getPassword(),
            joinRequest.getNickname(),
            joinRequest.getName(),
            joinRequest.getBirth(),
            Role.MEMBER,
            snsId,
            snsType,
            Status.NORMAL,
            now,
            now);
    }

    public void patchMemberRole(final Role role) {
        this.role = role;
    }

    public void patchMember(final String birth, final String name, final String nickname,
        final String password) {
        LocalDateTime now = LocalDateTime.now();
        if (!birth.isEmpty()) {
            this.birth = birth;
            this.modifiedAt = now;
        }
        if (!name.isEmpty()) {
            this.name = name;
            this.modifiedAt = now;
        }
        if (!nickname.isEmpty()) {
            this.nickname = nickname;
            this.modifiedAt = now;
        }
        if (!password.isEmpty()) {
            this.password = password;
            this.modifiedAt = now;
        }
    }
}
