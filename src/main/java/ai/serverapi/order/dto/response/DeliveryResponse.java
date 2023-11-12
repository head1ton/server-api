package ai.serverapi.order.dto.response;

import ai.serverapi.order.domain.Delivery;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class DeliveryResponse {

    private String ownerName;
    private String ownerZonecode;
    private String ownerAddress;
    private String ownerAddressDetail;
    private String ownerTel;

    private String recipientName;
    private String recipientZonecode;
    private String recipientAddress;
    private String recipientAddressDetail;
    private String recipientTel;

    public static DeliveryResponse fromDeliveryEntity(Delivery delivery) {
        return DeliveryResponse.builder()
                               .ownerName(delivery.getOwnerName())
                               .ownerZonecode(delivery.getOwnerZonecode())
                               .ownerAddress(delivery.getOwnerAddress())
                               .ownerAddressDetail(delivery.getOwnerAddressDetail())
                               .ownerTel(delivery.getOwnerTel())
                               .recipientName(delivery.getRecipientName())
                               .recipientZonecode(delivery.getRecipientZonecode())
                               .recipientAddress(delivery.getRecipientAddress())
                               .recipientAddressDetail(delivery.getRecipientAddressDetail())
                               .recipientTel(delivery.getRecipientTel())
                               .build();
    }


}
