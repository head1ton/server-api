package ai.serverapi.product.dto.response;

import ai.serverapi.product.domain.Product;
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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static CategoryResponse from(Product product) {
        return CategoryResponse.builder()
                               .categoryId(product.getCategory().getId())
                               .name(product.getCategory().getName())
                               .createdAt(product.getCategory().getCreatedAt())
                               .modifiedAt(product.getCategory().getModifiedAt())
                               .build();
    }

}
