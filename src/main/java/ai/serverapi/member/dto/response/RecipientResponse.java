package ai.serverapi.member.dto.response;

import ai.serverapi.member.enums.RecipientInfoStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecipientResponse(
    Long id,
    String name,
    String zonecode,
    String address,
    String tel,
    RecipientInfoStatus status,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {

}
