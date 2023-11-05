package ai.serverapi.domain.product.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductListVo(
    int totalPage,
    Long totalElements,
    int numberOfElements,
    Boolean last,
    Boolean empty,
    List<ProductVo> list) {
}
