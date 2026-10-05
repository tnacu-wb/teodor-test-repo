package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxNightsRuleEntity;


@Repository
public interface MaxNightsRepository extends JpaRepository<MaxNightsRuleEntity, Integer> {

  List<MaxNightsRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<MaxNightsRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status, LocalDateTime date);

  List<MaxNightsRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(String status,
      LocalDateTime date);
}
