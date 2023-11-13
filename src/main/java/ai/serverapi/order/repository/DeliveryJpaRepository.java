package ai.serverapi.order.repository;

import ai.serverapi.order.domain.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryJpaRepository extends JpaRepository<DeliveryEntity, Long> {

}
