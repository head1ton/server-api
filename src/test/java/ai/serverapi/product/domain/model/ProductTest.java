package ai.serverapi.product.domain.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ai.serverapi.order.controller.request.TempOrderDto;
import ai.serverapi.product.enums.ProductType;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductTest {

    @Test
    @DisplayName("재고가 부족하면 실패")
    void checkInStockFail1() {
        int ea = 10;
        Product product = Product.builder()
                                 .id(1L)
                                 .type(ProductType.NORMAL)
                                 .ea(ea)
                                 .build();
        TempOrderDto tempOrderDto = TempOrderDto.builder()
                                                .productId(1L)
                                                .ea(ea + 1)
                                                .build();
        assertThatThrownBy(() -> product.checkInStock(tempOrderDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고가 부족")
            .hasMessageContaining(String.valueOf(ea));
    }

    @Test
    @DisplayName("유효하지 않은 옵션 상품 실패")
    void checkInStockFail2() {
        int ea = 100;
        Product product = Product.builder()
                                 .id(1L)
                                 .type(ProductType.OPTION)
                                 .optionList(new ArrayList<>())
                                 .ea(ea)
                                 .build();
        TempOrderDto tempOrderDto = TempOrderDto.builder()
                                                .productId(1L)
                                                .optionId(0L)
                                                .ea(ea + 1)
                                                .build();
        assertThatThrownBy(() -> product.checkInStock(tempOrderDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("option id = 0");
    }

    @Test
    @DisplayName("재고가 부족하면 실패")
    void checkInStockFail_ea_option_id1() {
        int ea = 10;
        Product product = Product.builder()
                                 .id(1L)
                                 .type(ProductType.NORMAL)
                                 .ea(ea)
                                 .build();

        assertThatThrownBy(() -> product.checkInStock(ea + 1, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고가 부족")
            .hasMessageContaining(String.valueOf(ea));
    }

    @Test
    @DisplayName("유효하지 않은 옵션 상품 실패")
    void checkInStockFail_ea_option_id2() {
        int ea = 100;
        Product product = Product.builder()
                                 .id(1L)
                                 .type(ProductType.OPTION)
                                 .optionList(new ArrayList<>())
                                 .ea(ea)
                                 .build();

        assertThatThrownBy(() -> product.checkInStock(1, 0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("option id = 0");
    }

}
