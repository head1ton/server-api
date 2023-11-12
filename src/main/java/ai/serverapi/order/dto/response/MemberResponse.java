package ai.serverapi.order.dto.response;

import ai.serverapi.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
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
