package ai.serverapi.order.dto.response;

import ai.serverapi.product.domain.Category;
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
public class CategoryResponse {

    @NotNull
    private Long categoryId;

    @NotNull
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static CategoryResponse of(final Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getCreatedAt(),
            category.getModifiedAt());
    }
}
