package ai.serverapi.product.domain;

import ai.serverapi.product.dto.request.OptionRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    public Option(String name, int extraPrice, int ea, Product product) {
        LocalDateTime now = LocalDateTime.now();
        this.name = name;
        this.extraPrice = extraPrice;
        this.ea = ea;
        this.createdAt = now;
        this.modifiedAt = now;
        this.product = product;
    }

    public static Option of(Product product, OptionRequest optionRequest) {
        return new Option(optionRequest.getName(), optionRequest.getExtraPrice(),
            optionRequest.getEa(), product);
    }

    public void put(final OptionRequest optionRequest) {
        this.name = optionRequest.getName();
        this.extraPrice = optionRequest.getExtraPrice();
        this.ea = optionRequest.getEa();
        this.modifiedAt = LocalDateTime.now();
    }
}
