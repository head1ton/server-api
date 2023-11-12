package ai.serverapi.order.dto.response;

import ai.serverapi.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private String email;
    private String nickname;
    private String name;
    private String birth;

    public static MemberResponse fromMemberEntity(Member member) {
        return MemberResponse.builder()
                             .email(member.getEmail())
                             .name(member.getName())
                             .nickname(member.getNickname())
                             .birth(member.getBirth())
                             .build();
    }

}
