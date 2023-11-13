package ai.serverapi.product.domain.model;

import ai.serverapi.product.enums.OptionStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Option {

    private Long id;
    private String name;
    private int extraPrice;
    private int ea;
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Product product;
}
