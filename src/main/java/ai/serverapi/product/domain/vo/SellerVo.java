package ai.serverapi.product.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SellerVo(
    @NotNull Long sellerId,
    @NotNull String email,
    @NotNull String company,
    @NotNull String zonecode,
    @NotNull String address,
    @NotNull String tel
) {

}
