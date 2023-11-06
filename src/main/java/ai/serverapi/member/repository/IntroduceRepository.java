package ai.serverapi.member.repository;

import ai.serverapi.member.domain.entity.Introduce;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroduceRepository extends JpaRepository<Introduce, Long> {

}