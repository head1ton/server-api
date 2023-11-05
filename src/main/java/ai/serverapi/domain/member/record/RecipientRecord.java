package ai.serverapi.domain.member.record;

import ai.serverapi.domain.member.enums.RecipientInfoStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecipientRecord(
    Long id,
    String name,
    String address,
    String tel,
    RecipientInfoStatus status,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt) {
}
