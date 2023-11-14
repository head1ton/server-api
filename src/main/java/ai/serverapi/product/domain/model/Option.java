package ai.serverapi.product.domain.model;

import ai.serverapi.order.controller.request.TempOrderDto;
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

    public void checkInStock(final TempOrderDto tempOrderDto) {
        int ea = tempOrderDto.getEa();
        if (this.ea < ea) {
            throw new IllegalArgumentException(String.format("재고가 부족합니다.! 현재 재고 = %s개", this.ea));
        }
    }

    public void checkInStock(final int ea) {
        if (this.ea < ea) {
            throw new IllegalArgumentException(String.format("재고가 부족합니다.! 현재 재고 = %s개", this.ea));
        }
    }

    public void minusEa(final int ea) {
        this.ea -= ea;
    }
}
