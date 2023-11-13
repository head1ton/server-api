package ai.serverapi.product.repository;

import ai.serverapi.product.domain.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

}
