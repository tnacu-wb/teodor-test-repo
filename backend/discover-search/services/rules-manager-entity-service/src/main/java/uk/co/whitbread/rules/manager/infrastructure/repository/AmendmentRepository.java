package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.AmendmentRuleEntity;

public interface AmendmentRepository extends JpaRepository<AmendmentRuleEntity, Integer> {

  List<AmendmentRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<AmendmentRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status, LocalDateTime date);

  List<AmendmentRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(String status,
      LocalDateTime date);
}
