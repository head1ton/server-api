package ai.serverapi.product.domain;

import ai.serverapi.product.dto.request.OptionRequest;
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
public class Option {
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
    private Product product;

    public Option(String name, int extraPrice, int ea, Product product) {
        LocalDateTime now = LocalDateTime.now();
        this.name = name;
        this.extraPrice = extraPrice;
        this.ea = ea;
        this.createdAt = now;
        this.modifiedAt = now;
        this.status = OptionStatus.NORMAL;
        this.product = product;
    }

    public static Option of(Product product, OptionRequest optionRequest) {
        return new Option(optionRequest.getName(), optionRequest.getExtraPrice(),
            optionRequest.getEa(), product);
    }

    public static List<Option> ofList(final Product product,
        final List<OptionRequest> saveRequestOptionList) {
        List<Option> optionList = new ArrayList<>();
        for (OptionRequest optionRequest : saveRequestOptionList) {
            Option option = Option.of(product, optionRequest);
            optionList.add(option);
        }
        return optionList;
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
}
