package ai.serverapi.domain.vo.member;

import ai.serverapi.domain.enums.Role;
import ai.serverapi.domain.enums.member.SnsJoinType;
import ai.serverapi.domain.vo.BaseVo;
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
