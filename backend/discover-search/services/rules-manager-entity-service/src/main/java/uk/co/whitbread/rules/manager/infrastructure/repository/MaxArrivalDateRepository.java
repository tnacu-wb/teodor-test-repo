package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxArrivalDateRuleEntity;

@Repository
public interface MaxArrivalDateRepository extends JpaRepository<MaxArrivalDateRuleEntity, Integer> {

  List<MaxArrivalDateRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<MaxArrivalDateRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status, LocalDateTime date);

  List<MaxArrivalDateRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
      String status, LocalDateTime date);

}
