package ai.serverapi.order.repository.port;

import ai.serverapi.order.domain.model.OrderProduct;

public interface OrderProductRepository {

    void saveAll(Iterable<OrderProduct> orderProductList);

    OrderProduct save(OrderProduct createOrderProduct);

}
