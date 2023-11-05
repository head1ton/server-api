package ai.serverapi.member.repository;

import ai.serverapi.member.domain.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

}
