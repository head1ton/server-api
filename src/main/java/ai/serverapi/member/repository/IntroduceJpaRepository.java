package ai.serverapi.member.repository;

import ai.serverapi.product.domain.entity.IntroduceEntity;
import ai.serverapi.product.domain.entity.SellerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroduceJpaRepository extends JpaRepository<IntroduceEntity, Long> {

    Optional<IntroduceEntity> findBySeller(SellerEntity sellerEntity);
}