package ai.serverapi.order.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import ai.serverapi.member.domain.model.Member;
import ai.serverapi.member.enums.MemberRole;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.product.enums.ProductType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderItemTest {

    @Test
    @DisplayName("일반 상품의 OrderItem을 생성 성공")
    void createNormal() {
        Order order = Order.builder()
                           .id(1L)
                           .status(OrderStatus.TEMP)
                           .createdAt(LocalDateTime.now())
                           .modifiedAt(LocalDateTime.now())
                           .member(Member.builder()
                                         .id(1L)
                                         .nickname("nickname")
                                         .name("name")
                                         .email("email")
                                         .password("password")
                                         .role(MemberRole.MEMBER)
                                         .build())
                           .orderNumber("주문번호")
                           .build();

        OrderProduct orderProduct = OrderProduct.builder()
                                                .type(ProductType.NORMAL)
                                                .price(1000)
                                                .build();

        int ea = 2;

        OrderItem orderItem = OrderItem.create(order, orderProduct, null, ea);

        assertThat(orderItem.getProductTotalPrice()).isEqualTo(orderProduct.getPrice() * ea);
    }

    @Test
    @DisplayName("옵션 상품의 OrderItem을 생성 성공")
    void createOption() {
        Order order = Order.builder()
                           .id(1L)
                           .status(OrderStatus.TEMP)
                           .createdAt(LocalDateTime.now())
                           .modifiedAt(LocalDateTime.now())
                           .member(Member.builder()
                                         .id(1L)
                                         .nickname("nickname")
                                         .name("name")
                                         .email("email")
                                         .password("password")
                                         .role(MemberRole.MEMBER)
                                         .build())
                           .orderName("주문번호")
                           .build();

        OrderProduct orderProduct = OrderProduct.builder()
                                                .type(ProductType.OPTION)
                                                .price(1000)
                                                .build();

        OrderOption orderOption = OrderOption.builder()
                                             .extraPrice(100)
                                             .build();

        int ea = -3;

        OrderItem orderItem = OrderItem.create(order, orderProduct, orderOption, ea);

        assertThat(orderItem.getProductTotalPrice()).isEqualTo(
            (orderProduct.getPrice() + orderOption.getExtraPrice()) * ea);
    }

//    @Test
//    @DisplayName("getResponse 반환 데이터 생성에 성공")
//    void getResponse() {
//        //given
//        OrderItem orderItem = OrderItem.builder()
//                                       .order(Order.builder()
//                                                   .id(1L)
//                                                   .status(OrderStatus.TEMP)
//                                                   .createdAt(LocalDateTime.now())
//                                                   .modifiedAt(LocalDateTime.now())
//                                                   .member(Member.builder()
//                                                                 .id(1L)
//                                                                 .nickname("nickname")
//                                                                 .name("name")
//                                                                 .email("email")
//                                                                 .password("password")
//                                                                 .role(MemberRole.MEMBER)
//                                                                 .build())
//                                                   .orderName("주문번호")
//                                                   .build())
//                                       .ea(5)
//                                       .productPrice(1000)
//                                       .productTotalPrice(5000)
//                                       .status(OrderItemStatus.TEMP)
//                                       .orderProduct(OrderProduct.builder()
//                                                                 .type(ProductType.NORMAL)
//                                                                 .price(1000)
//                                                                 .build())
//                                       .build();
//        //when
//        OrderItem response = orderItem.toResponse();
//        //then
//        assertThat(response.getOrder()).isNull();
//    }
}
