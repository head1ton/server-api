package ai.serverapi.domain.vo.product;

import ai.serverapi.domain.vo.BaseVo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SellerVo extends BaseVo {

    @NotNull
    private Long memberId;
    @NotNull
    private String email;
    @NotNull
    private String nickname;
    @NotNull
    private String name;
}
