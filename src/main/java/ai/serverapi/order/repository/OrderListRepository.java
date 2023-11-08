package ai.serverapi.order.repository;

import ai.serverapi.order.domain.OrderList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderListRepository extends JpaRepository<OrderList, Long> {

}
