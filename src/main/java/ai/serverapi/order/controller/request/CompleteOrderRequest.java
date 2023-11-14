package ai.serverapi.order.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteOrderRequest {

    @JsonProperty("order_id")
    @NotNull(message = "order_id 필수입니다.")
    private Long orderId;

    @JsonProperty("owner_name")
    @NotNull(message = "owner_name 필수입니다.")
    private String ownerName;
    @JsonProperty("owner_zonecode")
    @NotNull(message = "owner_zonecode 필수입니다.")
    private String ownerZonecode;
    @JsonProperty("owner_address")
    @NotNull(message = "owner_address 필수입니다.")
    private String ownerAddress;
    @JsonProperty("owner_address_detail")
    @NotNull(message = "owner_address_detail 필수입니다.")
    private String ownerAddressDetail;
    @JsonProperty("owner_tel")
    @NotNull(message = "owner_tel 필수입니다.")
    private String ownerTel;

    @JsonProperty("recipient_name")
    @NotNull(message = "recipient_name 필수입니다.")
    private String recipientName;
    @JsonProperty("recipient_zonecode")
    @NotNull(message = "recipient_zonecode 필수입니다.")
    private String recipientZonecode;
    @JsonProperty("recipient_address")
    @NotNull(message = "recipient_address 필수입니다.")
    private String recipientAddress;
    @JsonProperty("recipient_address_detail")
    @NotNull(message = "recipient_address_detail 필수입니다.")
    private String recipientAddressDetail;
    @JsonProperty("recipient_tel")
    @NotNull(message = "recipient_tel 필수입니다.")
    private String recipientTel;

}
