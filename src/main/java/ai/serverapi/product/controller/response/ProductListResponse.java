package ai.serverapi.product.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public class ProductListResponse {

    private int totalPage;
    private Long totalElements;
    private int numberOfElements;
    private Boolean last;
    private Boolean empty;
    private List<ProductResponse> list;

    public static ProductListResponse from(Page<ProductResponse> page) {
        return ProductListResponse.builder()
                                  .totalPage(page.getTotalPages())
                                  .totalElements(page.getTotalElements())
                                  .numberOfElements(page.getNumberOfElements())
                                  .last(page.isLast())
                                  .empty(page.isEmpty())
                                  .list(page.getContent())
                                  .build();
    }
}
