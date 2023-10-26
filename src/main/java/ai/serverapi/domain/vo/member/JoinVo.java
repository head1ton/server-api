package ai.serverapi.domain.vo.member;

import ai.serverapi.domain.vo.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinVo extends BaseVo {

    private String name;
    private String nickname;
    private String email;
}
