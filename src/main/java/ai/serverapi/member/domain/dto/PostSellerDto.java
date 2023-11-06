package ai.serverapi.member.domain.dto;

import jakarta.persistence.Column;
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
public class PostSellerDto {

    @NotNull(message = "company 필수입니다.")
    private String company;
    @NotNull(message = "tel 필수입니다.")
    @Column(length = 11)
    private String tel;
    @NotNull(message = "zonecode 필수입니다.")
    private String zonecode;
    @NotNull(message = "address 필수입니다.")
    private String address;
    @Email(message = "email 형식을 맞춰주세요.")
    private String email;
}
