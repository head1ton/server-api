package ai.serverapi.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorDto {

    @NotNull(message = "point 는 필수입니다.")
    private String point;
    @NotNull(message = "detail 은 필수입니다.")
    private String detail;
}
