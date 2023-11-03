package ai.serverapi.repository.member;

import ai.serverapi.domain.entity.member.BuyerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerInfoRepository extends JpaRepository<BuyerInfo, Long> {

}
