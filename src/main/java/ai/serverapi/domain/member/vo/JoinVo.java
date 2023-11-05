package ai.serverapi.domain.member.vo;

import ai.serverapi.config.base.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinVo extends BaseVo {

    private String name;
    private String nickname;
    private String email;
}
