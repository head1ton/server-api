package ai.serverapi.member.domain.model;

import ai.serverapi.member.domain.entity.RecipientEntity;
import ai.serverapi.member.enums.MemberRole;
import ai.serverapi.member.enums.MemberStatus;
import ai.serverapi.member.enums.SnsJoinType;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Member {

    private final Long id;
    private final String email;
    private final String password;
    private final String nickname;
    private final String name;
    private final String birth;
    private final MemberRole role;
    private final String snsId;
    private final SnsJoinType snsType;
    private final MemberStatus status;
    private List<RecipientEntity> recipientList = new LinkedList<>();
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public Member(final Long id, final String email, final String password, final String nickname,
        final String name, final String birth,
        final MemberRole role, final String snsId, final SnsJoinType snsType,
        final MemberStatus status,
        final List<RecipientEntity> recipientList, final LocalDateTime createdAt,
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
        this.status = status;
        this.recipientList = recipientList;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
