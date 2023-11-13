package ai.serverapi.order.dto.response;

import ai.serverapi.order.domain.entity.OrderItemEntity;
import ai.serverapi.order.domain.vo.SellerVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReceiptResponse {

    private SellerVo seller;
    private String productName;
    private int ea;
    private int totalPrice;

    public static ReceiptResponse of(OrderItemEntity orderItemEntity) {
        return new ReceiptResponse(SellerVo.of(orderItemEntity.getProduct().getSeller()),
            orderItemEntity.getProduct().getMainTitle(), orderItemEntity.getEa(),
            orderItemEntity.getProductTotalPrice());
    }
}
