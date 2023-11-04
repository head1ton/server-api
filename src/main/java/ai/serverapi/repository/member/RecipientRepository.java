package ai.serverapi.repository.member;

import ai.serverapi.domain.entity.member.RecipientInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<RecipientInfo, Long> {

}
