package ai.serverapi.domain.member.vo;

import ai.serverapi.config.base.BaseVo;
import ai.serverapi.domain.member.enums.Role;
import ai.serverapi.domain.member.enums.SnsJoinType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberVo extends BaseVo {

    private Long memberId;
    private String email;
    private String nickname;
    private String name;
    private Role role;
    private SnsJoinType snsType;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}