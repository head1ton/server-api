package ai.serverapi.member.dto.response;

import ai.serverapi.member.enums.Role;
import ai.serverapi.member.enums.SnsJoinType;
import ai.serverapi.member.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;


@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MemberResponse(
    Long memberId,
    String email,
    String nickname,
    String name,
    Role role,
    SnsJoinType snsType,
    Status status,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {

}