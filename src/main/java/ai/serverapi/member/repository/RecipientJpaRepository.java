package ai.serverapi.member.repository;

import ai.serverapi.member.domain.entity.RecipientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientJpaRepository extends JpaRepository<RecipientEntity, Long> {

}
