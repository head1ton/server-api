package ai.serverapi.member.domain.entity;

import ai.serverapi.member.controller.request.JoinRequest;
import ai.serverapi.member.domain.model.Member;
import ai.serverapi.member.enums.MemberRole;
import ai.serverapi.member.enums.MemberStatus;
import ai.serverapi.member.enums.SnsJoinType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class MemberEntity {

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private final List<RecipientEntity> recipientList = new LinkedList<>();

    private String snsId;
    @Enumerated(EnumType.STRING)
    private SnsJoinType snsType;
    @Enumerated(EnumType.STRING)
    private MemberRole role;
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime modifiedAt = LocalDateTime.now();

    public MemberEntity(
        final String email,
        final String password,
        final String nickname,
        final String name,
        final String birth,
        final MemberRole role,
        final String snsId,
        final SnsJoinType snsType,
        final MemberStatus status,
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

    public MemberEntity(
        final Long id,
        final String email,
        final String password,
        final String nickname,
        final String name,
        final String birth,
        final MemberRole role,
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

    public static MemberEntity from(Member member) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.id = member.getId();
        memberEntity.email = member.getEmail();
        memberEntity.password = member.getPassword();
        memberEntity.nickname = member.getNickname();
        memberEntity.name = member.getName();
        memberEntity.birth = member.getBirth();
        memberEntity.role = member.getRole();
        memberEntity.snsId = member.getSnsId();
        memberEntity.snsType = member.getSnsType();
        memberEntity.status = member.getStatus();
        memberEntity.createdAt = member.getCreatedAt();
        memberEntity.modifiedAt = member.getModifiedAt();
        return memberEntity;
    }

    public static MemberEntity of(final JoinRequest joinRequest) {
        LocalDateTime now = LocalDateTime.now();
        return new MemberEntity(joinRequest.getEmail(),
            joinRequest.getPassword(),
            joinRequest.getNickname(),
            joinRequest.getName(),
            joinRequest.getBirth(),
            MemberRole.MEMBER,
            null,
            null,
            MemberStatus.NORMAL,
            now,
            now);
    }

    public static MemberEntity of(final JoinRequest joinRequest, String snsId,
        SnsJoinType snsType) {
        LocalDateTime now = LocalDateTime.now();
        return new MemberEntity(joinRequest.getEmail(),
            joinRequest.getPassword(),
            joinRequest.getNickname(),
            joinRequest.getName(),
            joinRequest.getBirth(),
            MemberRole.MEMBER,
            snsId,
            snsType,
            MemberStatus.NORMAL,
            now,
            now);
    }

    public Member toModel() {
        return Member.builder()
                     .id(id)
                     .email(email)
                     .password(password)
                     .nickname(nickname)
                     .name(name)
                     .birth(birth)
                     .role(role)
                     .snsId(snsId)
                     .snsType(snsType)
                     .status(status)
                     .createdAt(createdAt)
                     .modifiedAt(modifiedAt)
                     .build();
    }

    public void patchMemberRole(final MemberRole memberRole) {
        this.role = memberRole;
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
