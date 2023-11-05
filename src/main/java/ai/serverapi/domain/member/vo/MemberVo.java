package ai.serverapi.domain.member.vo;

import ai.serverapi.domain.member.enums.Role;
import ai.serverapi.domain.member.enums.SnsJoinType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;


@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MemberVo(
    Long memberId,
    String email,
    String nickname,
    String name,
    Role role,
    SnsJoinType snsType,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {

}
