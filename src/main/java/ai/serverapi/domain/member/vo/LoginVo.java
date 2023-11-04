package ai.serverapi.domain.member.vo;

import ai.serverapi.config.base.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginVo extends BaseVo {

    private String type;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpired;
    private Long refreshTokenExpired;
}
