package ai.serverapi.domain.member.repository;

import ai.serverapi.domain.member.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

}