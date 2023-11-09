package ai.serverapi.member.repository;

import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByMember(Member member);

    Optional<Seller> findByCompany(String company);
}