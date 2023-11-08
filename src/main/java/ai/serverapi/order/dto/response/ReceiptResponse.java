package ai.serverapi.order.dto.response;

import ai.serverapi.order.domain.OrdersDetail;
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

    private SellerResponse seller;
    private String productName;
    private int ea;
    private int totalPrice;

    public static ReceiptResponse of(OrdersDetail ordersDetail) {
        return new ReceiptResponse(SellerResponse.of(ordersDetail.getProduct().getSeller()),
            ordersDetail.getProduct().getMainTitle(), ordersDetail.getEa(),
            ordersDetail.getProductTotalPrice());
    }
}
