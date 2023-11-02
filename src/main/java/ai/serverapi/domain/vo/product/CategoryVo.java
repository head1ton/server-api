package ai.serverapi.domain.vo.product;

import ai.serverapi.domain.vo.BaseVo;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CategoryVo extends BaseVo {

    @NotNull
    private Long categoryId;
    @NotNull
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
