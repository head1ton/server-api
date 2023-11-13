package ai.serverapi.product.repository;

import ai.serverapi.product.domain.entity.OptionEntity;
import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.enums.OptionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionJpaRepository extends JpaRepository<OptionEntity, Long> {

    List<OptionEntity> findByProduct(ProductEntity productEntity);

    List<OptionEntity> findByProductAndStatus(ProductEntity productEntity,
        OptionStatus optionStatus);
}
