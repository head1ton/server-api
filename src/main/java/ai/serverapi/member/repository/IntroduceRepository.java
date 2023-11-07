package ai.serverapi.member.repository;

import ai.serverapi.member.domain.Introduce;
import ai.serverapi.member.domain.Seller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroduceRepository extends JpaRepository<Introduce, Long> {

    Optional<Introduce> findBySeller(Seller seller);
}