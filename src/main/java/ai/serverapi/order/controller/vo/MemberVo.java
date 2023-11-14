package ai.serverapi.order.controller.vo;

import ai.serverapi.member.domain.entity.MemberEntity;
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
public class MemberVo {

    private String email;
    private String nickname;
    private String name;
    private String birth;

    public static MemberVo fromMemberEntity(MemberEntity memberEntity) {
        return MemberVo.builder()
                       .email(memberEntity.getEmail())
                       .name(memberEntity.getName())
                       .nickname(memberEntity.getNickname())
                       .birth(memberEntity.getBirth())
                       .build();
    }

}
