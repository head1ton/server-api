package ai.serverapi.product.repository;

import ai.serverapi.product.domain.Option;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.enums.OptionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByProduct(Product product);

    List<Option> findByProductAndStatus(Product product, OptionStatus optionStatus);
}
