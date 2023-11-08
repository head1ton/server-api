package ai.serverapi.order.repository;

import ai.serverapi.order.domain.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersDetailRepostiory extends JpaRepository<OrdersDetail, Long> {

}
