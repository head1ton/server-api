package ai.serverapi.order.repository.port;

import ai.serverapi.order.domain.model.Delivery;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);
}
