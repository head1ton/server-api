package ai.serverapi.product.domain.model;

import ai.serverapi.product.controller.response.CategoryResponse;
import ai.serverapi.product.enums.CategoryStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Category {

    private Long id;
    private String name;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CategoryResponse toResponse() {
        return CategoryResponse.builder()
                               .categoryId(id)
                               .name(name)
                               .status(status)
                               .createdAt(createdAt)
                               .modifiedAt(modifiedAt)
                               .build();
    }
}
