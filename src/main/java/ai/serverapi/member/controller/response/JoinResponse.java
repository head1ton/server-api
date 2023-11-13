package ai.serverapi.member.controller.response;

import ai.serverapi.member.domain.entity.MemberEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@Getter
public class JoinResponse {

    private String name;
    private String nickname;
    private String email;

    public static JoinResponse from(MemberEntity saveMemberEntity) {
        return JoinResponse.builder()
                           .name(saveMemberEntity.getName())
                           .nickname(saveMemberEntity.getNickname())
                           .email(saveMemberEntity.getEmail())
                           .build();
    }
}
