package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RbacRuleEntity;

@Repository
public interface RbacRuleRepository extends JpaRepository<RbacRuleEntity, Integer> {

  List<RbacRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<RbacRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
      String status, LocalDateTime date);

  List<RbacRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status, LocalDateTime date);
}
