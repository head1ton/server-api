package ai.serverapi.global.base;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDto {

    @NotNull(message = "point 는 필수입니다.")
    private String point;
    @NotNull(message = "detail 은 필수입니다.")
    private String detail;
}
