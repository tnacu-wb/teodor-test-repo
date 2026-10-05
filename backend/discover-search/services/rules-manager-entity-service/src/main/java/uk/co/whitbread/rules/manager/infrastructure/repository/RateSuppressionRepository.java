package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RateSuppressionRuleEntity;

@Repository
public interface RateSuppressionRepository extends JpaRepository<RateSuppressionRuleEntity, Integer> {

  List<RateSuppressionRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<RateSuppressionRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status, LocalDateTime date);

  List<RateSuppressionRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
      String status,
      LocalDateTime date);
}
