package ai.serverapi.order.controller.vo;

import ai.serverapi.product.domain.entity.CategoryEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
public class CategoryVo {

    @NotNull
    private Long categoryId;

    @NotNull
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static CategoryVo of(final CategoryEntity categoryEntity) {
        return new CategoryVo(
            categoryEntity.getId(), categoryEntity.getName(), categoryEntity.getCreatedAt(),
            categoryEntity.getModifiedAt());
    }
}
