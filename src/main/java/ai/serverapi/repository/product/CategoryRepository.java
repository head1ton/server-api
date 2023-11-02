package ai.serverapi.repository.product;

import ai.serverapi.domain.entity.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
