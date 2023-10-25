package ai.serverapi.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestDto {

    @NotNull(message = "id는 필수 정보입니다.")
    private String id;
    @NotNull(message = "age는 필수 정보입니다.")
    @Min(value = 10, message = "age는 최소 10살입니다.")
    @Max(value = 100, message = "age는 최대 100살입니다.")
    private String age;
}
