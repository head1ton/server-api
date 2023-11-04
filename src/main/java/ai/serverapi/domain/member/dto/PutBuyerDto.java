package ai.serverapi.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PutBuyerDto {

    @NotNull(message = "id 는 필수입니다.")
    private Long id;
    @NotNull(message = "name 은 필수입니다.")
    private String name;
    @NotNull(message = "email 은 필수입니다.")
    @Email(message = "email 형식을 지켜주세요.")
    private String email;
    @NotNull(message = "tel 은 필수입니다.")
    private String tel;
}
