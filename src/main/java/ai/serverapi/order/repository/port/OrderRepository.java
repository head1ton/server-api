package ai.serverapi.order.repository.port;

import ai.serverapi.order.domain.model.Order;

public interface OrderRepository {

    Order save(Order order);

    Order findById(Long orderId);
}
