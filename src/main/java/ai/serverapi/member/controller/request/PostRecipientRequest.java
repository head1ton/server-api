package ai.serverapi.member.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRecipientRequest {

    @NotNull(message = "name 필수입니다.")
    private String name;
    @NotNull(message = "zonecode 필수입니다.")
    private String zonecode;
    @NotNull(message = "address 필수입니다.")
    private String address;
    @NotNull(message = "addressDetail 필수입니다.")
    private String addressDetail;
    @NotNull(message = "tel 필수입니다.")
    private String tel;
}
