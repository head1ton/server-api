package ai.serverapi.product.domain.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ai.serverapi.order.controller.request.TempOrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OptionTest {

    @Test
    @DisplayName("요청 ea가 option의 ea보다 크다면 실패")
    void checkInStockFail1() {
        int ea = 10;
        Option option = Option.builder()
                              .id(1L)
                              .ea(ea)
                              .build();

        TempOrderDto tempOrderDto = TempOrderDto.builder()
                                                .optionId(1L)
                                                .ea(ea + 1)
                                                .build();

        assertThatThrownBy(() -> option.checkInStock(tempOrderDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고");
    }

    @Test
    @DisplayName("요청 ea가 option의 ea보다 크다면 실패")
    void checkInStockFail_ea_option_id() {
        int ea = 10;
        Option option = Option.builder()
                              .id(1L)
                              .ea(ea)
                              .build();

        assertThatThrownBy(() -> option.checkInStock(ea + 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고");
    }
}
