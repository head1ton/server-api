package ai.serverapi.product.repository;

import ai.serverapi.product.domain.entity.CategoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByName(String categoryName);
}
