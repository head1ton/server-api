package ai.serverapi.domain.dto.member;

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
public class PostBuyerInfoDto {

    private Long id;
    @NotNull(message = "name 비어있을 수 없습니다.")
    private String name;
    @Email(message = "email 형식을 지켜주세요.")
    @NotNull(message = "email 비어있을 수 없습니다.")
    private String email;
    @NotNull(message = "tel 비어있을 수 없습니다.")
    private String tel;
}
