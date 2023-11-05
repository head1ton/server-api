package ai.serverapi.domain.product.dto;

import ai.serverapi.config.base.BaseVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddViewCntDto extends BaseVo {

    @NotNull(message = "product_id 비어 있을 수 없습니다.")
    @JsonProperty("product_id")
    private Long product_id;
}
