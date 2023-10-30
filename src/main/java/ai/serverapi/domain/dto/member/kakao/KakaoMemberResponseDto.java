package ai.serverapi.domain.dto.member.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMemberResponseDto {

    public Long id;
    public KakaoAccountDto kakao_account;
}
