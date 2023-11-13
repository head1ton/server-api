package ai.serverapi.member.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class JoinRequest {

    @NotNull(message = "email은 필수입니다.")
    @Email(message = "email 형식을 맞춰주세요.", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$")
    private String email;
    @NotNull(message = "password는 필수입니다.")
    private String password;
    @NotNull(message = "name은 필수입니다.")
    private String name;
    @NotNull(message = "nickname은 필수입니다.")
    private String nickname;
    private String birth;

    public JoinRequest(final String email, final String password, final String name,
        final String nickname, final String birth) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
    }

    public void passwordEncoder(final BCryptPasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
