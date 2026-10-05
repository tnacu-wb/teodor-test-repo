package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BusinessAllowanceRuleEntity;

@Repository
public interface BusinessAllowanceRuleRepository extends
    JpaRepository<BusinessAllowanceRuleEntity, Integer> {

  List<BusinessAllowanceRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<BusinessAllowanceRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status,
      LocalDateTime date);

  List<BusinessAllowanceRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
      String status,
      LocalDateTime date);

}
