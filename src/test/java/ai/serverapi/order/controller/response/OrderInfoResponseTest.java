package ai.serverapi.order.controller.response;

import ai.serverapi.order.domain.model.Order;
import ai.serverapi.order.domain.model.OrderItem;
import ai.serverapi.order.domain.model.OrderProduct;
import ai.serverapi.product.domain.model.Category;
import ai.serverapi.product.domain.model.Seller;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderInfoResponseTest {


    @Test
    @DisplayName("OrderInfoResponse create에 성공")
    void create() {
        //given
        Order order = Order.builder()
                           .id(1L)
                           .orderItemList(List.of(
                               OrderItem.builder()
                                        .id(1L)
                                        .orderProduct(OrderProduct.builder()
                                                                  .id(2L)
                                                                  .productId(2L)
                                                                  .seller(Seller.builder().build())
                                                                  .category(
                                                                      Category.builder().build())
                                                                  .mainTitle("상품2")
                                                                  .build())
                                        .build(),
                               OrderItem.builder()
                                        .id(2L)
                                        .orderProduct(OrderProduct.builder()
                                                                  .id(1L)
                                                                  .productId(1L)
                                                                  .seller(Seller.builder().build())
                                                                  .category(
                                                                      Category.builder().build())
                                                                  .mainTitle("상품1")
                                                                  .build())
                                        .build()
                           ))
                           .build();
        //when
        OrderInfoResponse orderInfoResponse = OrderInfoResponse.create(order);
        //then
        Assertions.assertThat(orderInfoResponse.getOrderItemList().get(0).getProductId())
                  .isEqualTo(2L);
    }
}
