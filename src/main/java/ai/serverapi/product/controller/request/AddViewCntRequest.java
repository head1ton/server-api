package ai.serverapi.product.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddViewCntRequest {

    @NotNull(message = "product_id 비어 있을 수 없습니다.")
    @JsonProperty("product_id")
    @NotNull
    private Long productId;
}
