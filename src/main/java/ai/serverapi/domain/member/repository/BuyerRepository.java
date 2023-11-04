package ai.serverapi.domain.member.repository;

import ai.serverapi.domain.member.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {

}
