package ai.serverapi.domain.vo.member;

import ai.serverapi.domain.vo.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginVo extends BaseVo {

    private String type;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpired;
}
