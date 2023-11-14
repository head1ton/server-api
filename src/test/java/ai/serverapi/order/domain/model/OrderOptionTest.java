package ai.serverapi.order.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ai.serverapi.product.domain.model.Option;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderOptionTest {

    @Test
    @DisplayName("option id가 없으면 실패")
    void createFail1() {
        List<Option> optionList = List.of(
            Option.builder()
                  .id(1L)
                  .name("option1")
                  .build(),
            Option.builder()
                  .id(2L)
                  .name("option2")
                  .build()
        );

        assertThatThrownBy(() -> OrderOption.create(optionList, 3L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("option id = 3");
    }

    @Test
    @DisplayName("OrderOption create 성공")
    void create() {
        Long targetId = 1L;

        List<Option> optionList = List.of(
            Option.builder()
                  .id(targetId)
                  .name("option1")
                  .build(),
            Option.builder()
                  .id(2L)
                  .name("option2")
                  .build()
        );

        OrderOption orderOption = OrderOption.create(optionList, targetId);
        assertThat(orderOption.getId()).isEqualTo(targetId);
    }

}
