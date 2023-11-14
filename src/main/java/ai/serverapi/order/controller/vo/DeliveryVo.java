package ai.serverapi.order.controller.vo;

import ai.serverapi.order.domain.entity.DeliveryEntity;
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
public class DeliveryVo {

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

    public static DeliveryVo fromDeliveryEntity(DeliveryEntity deliveryEntity) {
        return DeliveryVo.builder()
                         .ownerName(deliveryEntity.getOwnerName())
                         .ownerZonecode(deliveryEntity.getOwnerZonecode())
                         .ownerAddress(deliveryEntity.getOwnerAddress())
                         .ownerAddressDetail(deliveryEntity.getOwnerAddressDetail())
                         .ownerTel(deliveryEntity.getOwnerTel())
                         .recipientName(deliveryEntity.getRecipientName())
                         .recipientZonecode(deliveryEntity.getRecipientZonecode())
                         .recipientAddress(deliveryEntity.getRecipientAddress())
                         .recipientAddressDetail(deliveryEntity.getRecipientAddressDetail())
                         .recipientTel(deliveryEntity.getRecipientTel())
                         .build();
    }


}
