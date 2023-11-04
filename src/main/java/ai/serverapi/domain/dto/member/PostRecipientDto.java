package ai.serverapi.domain.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRecipientDto {

    @NotNull(message = "name 필수입니다.")
    private String name;
    @NotNull(message = "address 필수입니다.")
    private String address;
    @NotNull(message = "tel 필수입니다.")
    private String tel;
}
