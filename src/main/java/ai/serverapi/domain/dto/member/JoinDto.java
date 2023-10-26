package ai.serverapi.domain.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto {

    @NotNull(message = "email은 필수입니다.")
    @Email(message = "email 형식을 맞춰주세요.", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$")
    private String email;
    @NotNull(message = "password는 필수입니다.")
    private String password;
    @NotNull(message = "name은 필수입니다.")
    private String name;
    @NotNull(message = "nickname은 필수입니다.")
    private String nickname;
    @NotNull(message = "birth는 필수입니다.")
    private String birth;
}
