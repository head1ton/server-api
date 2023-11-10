package ai.serverapi.product.repository;

import ai.serverapi.product.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {

}
