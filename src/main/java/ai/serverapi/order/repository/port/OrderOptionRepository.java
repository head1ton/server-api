package ai.serverapi.order.repository.port;

import ai.serverapi.order.domain.model.OrderOption;

public interface OrderOptionRepository {

    void saveAll(Iterable<OrderOption> orderOptionList);

    OrderOption save(OrderOption createOrderOption);

}
