package ai.serverapi.order.domain.entity;

import ai.serverapi.order.domain.model.OrderOption;
import ai.serverapi.product.enums.OptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_option")
public class OrderOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_option_id")
    private Long id;
    private Long optionId;
    private String name;
    private int extraPrice;
    @Enumerated(EnumType.STRING)
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderOptionEntity from(OrderOption orderOption) {
        OrderOptionEntity orderOptionEntity = new OrderOptionEntity();
        orderOptionEntity.id = orderOption.getId();
        orderOptionEntity.optionId = orderOption.getId();
        orderOptionEntity.name = orderOption.getName();
        orderOptionEntity.extraPrice = orderOption.getExtraPrice();
        orderOptionEntity.status = orderOption.getStatus();
        orderOptionEntity.createdAt = orderOption.getCreatedAt();
        orderOptionEntity.modifiedAt = orderOption.getModifiedAt();
        return orderOptionEntity;
    }

    public OrderOption toModel() {
        return OrderOption.builder()
                          .id(id)
                          .optionId(optionId)
                          .name(name)
                          .extraPrice(extraPrice)
                          .status(status)
                          .createdAt(createdAt)
                          .modifiedAt(modifiedAt)
                          .build();
    }
}
