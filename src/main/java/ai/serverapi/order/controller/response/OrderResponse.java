package ai.serverapi.order.controller.response;

import ai.serverapi.order.controller.vo.OrderVo;
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
public class OrderResponse {

    private int totalPage;
    private Long totalElements;
    private int numberOfElements;
    private Boolean last;
    private Boolean empty;
    private List<OrderVo> list;

    public static OrderResponse from(Page<OrderVo> orderPage) {
        return OrderResponse.builder()
                            .totalPage(orderPage.getTotalPages())
                            .totalElements(orderPage.getTotalElements())
                            .numberOfElements(orderPage.getNumberOfElements())
                            .last(orderPage.isLast())
                            .empty(orderPage.isEmpty())
                            .list(orderPage.getContent())
                            .build();
    }
}
