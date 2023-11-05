package ai.serverapi.domain.member.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LoginRecord(
    String type,
    String accessToken,
    String refreshToken,
    Long accessTokenExpired,
    Long refreshTokenExpired) {
}
