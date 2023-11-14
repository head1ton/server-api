package ai.serverapi.product.controller.response;

import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.enums.CategoryStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(Include.NON_NULL)
@JsonNaming(SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public final class CategoryResponse {

    @NotNull
    private Long categoryId;
    @NotNull
    private String name;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static CategoryResponse from(ProductEntity productEntity) {
        return CategoryResponse.builder()
                               .categoryId(productEntity.getCategory().getId())
                               .name(productEntity.getCategory().getName())
                               .createdAt(productEntity.getCategory().getCreatedAt())
                               .modifiedAt(productEntity.getCategory().getModifiedAt())
                               .build();
    }

}
