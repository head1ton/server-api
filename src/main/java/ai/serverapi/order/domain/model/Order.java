package ai.serverapi.order.domain.model;

import ai.serverapi.member.domain.model.Member;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.product.domain.model.Product;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Order {

    private Long id;
    private Member member;
    private String orderNumber;
    private List<OrderItem> orderItemList;
    private List<Delivery> deliveryList;
    private OrderStatus status;
    private String orderName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static Order create(Member member, List<Product> productList) {
        int productListSize = productList.size();
        String productExtraName = productListSize > 1 ? " 외 " + (productListSize - 1) + "개" : "";
        final String orderNameBuilder = productList.get(0).getMainTitle()
            + productExtraName;

        return Order.builder()
                    .member(member)
                    .orderItemList(new ArrayList<>())
                    .status(OrderStatus.TEMP)
                    .orderName(orderNameBuilder)
                    .createdAt(LocalDateTime.now())
                    .modifiedAt(LocalDateTime.now())
                    .build();
    }

    public void createOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        final String orderNumberBuilder = "ORDER"
            + "-"
            + now.format(formatter)
            + "-"
            + id;

        this.orderNumber = orderNumberBuilder;
    }

    public void checkOrder(Member member) {
        if (!this.member.getId().equals(member.getId())) {
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }
    }

    public void complete() {
        this.status = OrderStatus.COMPLETE;
    }
}
