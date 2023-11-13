package ai.serverapi.member.controller.response.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoLoginResponse {

    public String access_token;
    public String refresh_token;
    public Long expires_in;
    public Long refresh_token_expires_in;
}
