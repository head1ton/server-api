package ai.serverapi.order.repository.port;

import ai.serverapi.order.domain.model.OrderItem;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    void saveAll(Iterable<OrderItem> orderItemList);
}
