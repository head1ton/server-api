package ai.serverapi.product.domain.entity;

import ai.serverapi.product.controller.request.OptionRequest;
import ai.serverapi.product.domain.model.Option;
import ai.serverapi.product.enums.OptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "options")
public class OptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    private String name;
    private int extraPrice;
    private int ea;
    @Enumerated(EnumType.STRING)
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    public OptionEntity(String name, int extraPrice, int ea, ProductEntity product) {
        LocalDateTime now = LocalDateTime.now();
        this.name = name;
        this.extraPrice = extraPrice;
        this.ea = ea;
        this.createdAt = now;
        this.modifiedAt = now;
        this.status = OptionStatus.NORMAL;
        this.product = product;
    }

    public Option toModel() {
        return Option.builder()
                     .id(id)
                     .name(name)
                     .extraPrice(extraPrice)
                     .ea(ea)
                     .status(status)
                     .createdAt(createdAt)
                     .modifiedAt(modifiedAt)
                     .build();
    }

    public static OptionEntity from(Option option) {
        OptionEntity optionEntity = new OptionEntity();
        optionEntity.id = option.getId();
        optionEntity.name = option.getName();
        optionEntity.extraPrice = option.getExtraPrice();
        optionEntity.ea = option.getEa();
        optionEntity.status = option.getStatus();
        optionEntity.createdAt = option.getCreatedAt();
        optionEntity.modifiedAt = option.getModifiedAt();
        return optionEntity;
    }

    public static OptionEntity of(ProductEntity productEntity, OptionRequest optionRequest) {
        return new OptionEntity(optionRequest.getName(), optionRequest.getExtraPrice(),
            optionRequest.getEa(), productEntity);
    }

    public static List<OptionEntity> ofList(final ProductEntity productEntity,
        final List<OptionRequest> saveRequestOptionList) {
        List<OptionEntity> optionEntityList = new ArrayList<>();
        for (OptionRequest optionRequest : saveRequestOptionList) {
            OptionEntity optionEntity = OptionEntity.of(productEntity, optionRequest);
            optionEntityList.add(optionEntity);
        }
        return optionEntityList;
    }

    public void put(final OptionRequest optionRequest) {
        this.name = optionRequest.getName();
        this.extraPrice = optionRequest.getExtraPrice();
        this.ea = optionRequest.getEa();
        this.modifiedAt = LocalDateTime.now();
        this.status = OptionStatus.valueOf(
            Optional.ofNullable(optionRequest.getStatus()).orElse("NORMAL").toUpperCase());
    }

    public void delete() {
        this.status = OptionStatus.DELETE;
    }

    public void minusEa(final int ea) {
        this.ea -= ea;
    }
}
