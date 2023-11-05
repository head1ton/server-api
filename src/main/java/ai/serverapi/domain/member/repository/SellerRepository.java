package ai.serverapi.domain.member.repository;

import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.entity.Seller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByMember(Member member);
}