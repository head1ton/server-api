package ai.serverapi.member.controller.response.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoProfile {

    public String nickname;
    public String thumbnail_image_url;
    public String profile_image_url;
}
