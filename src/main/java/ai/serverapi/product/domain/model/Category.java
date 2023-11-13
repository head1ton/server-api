package ai.serverapi.product.domain.model;

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
}
