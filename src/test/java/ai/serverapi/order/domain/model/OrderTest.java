package ai.serverapi.order.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ai.serverapi.member.domain.model.Member;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.product.domain.model.Product;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderTest {

    @Test
    @DisplayName("order create 성공")
    void create() {
        Member member = Member.builder().build();

        List<Product> productList = List.of(
            Product.builder()
                   .mainTitle("상품1")
                   .build(),
            Product.builder()
                   .mainTitle("상품2")
                   .build(),
            Product.builder()
                   .mainTitle("상품3")
                   .build()
        );

        Order order = Order.create(member, productList);

        assertThat(order.getOrderName()).isEqualTo("상품1 외 2개");
    }

    @Test
    @DisplayName("order number 성공")
    void createOrderNumber() {
        Order order = Order.builder()
                           .id(1L)
                           .build();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        order.createOrderNumber();

        assertThat(order.getOrderNumber()).isEqualTo(
            "ORDER-" + formatter.format(now) + "-" + order.getId());
    }

    @Test
    @DisplayName("유저 정보가 다르면 order check 실패")
    void checkOrderFail1() {
        //given
        Member member = Member.builder()
                              .id(1L)
                              .build();
        Order order = Order.builder()
                           .id(1L)
                           .member(Member.builder()
                                         .id(2L)
                                         .build())
                           .status(OrderStatus.TEMP)
                           .build();
        //when
        //then
        assertThatThrownBy(() -> order.checkOrder(member))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 주문");
    }

}
