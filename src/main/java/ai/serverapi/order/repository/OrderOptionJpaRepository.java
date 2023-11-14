package ai.serverapi.order.repository;

import ai.serverapi.order.domain.entity.OrderOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderOptionJpaRepository extends JpaRepository<OrderOptionEntity, Long> {

}
