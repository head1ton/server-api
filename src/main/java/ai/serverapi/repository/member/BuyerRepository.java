package ai.serverapi.repository.member;

import ai.serverapi.domain.entity.member.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {

}
