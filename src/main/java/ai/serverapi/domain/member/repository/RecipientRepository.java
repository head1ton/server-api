package ai.serverapi.domain.member.repository;

import ai.serverapi.domain.member.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

}
