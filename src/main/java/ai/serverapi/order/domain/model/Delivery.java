package ai.serverapi.order.domain.model;

import ai.serverapi.order.enums.DeliveryStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Delivery {

    private Long id;
    private Order order;
    private OrderItem orderItem;
    private DeliveryStatus status;

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

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
