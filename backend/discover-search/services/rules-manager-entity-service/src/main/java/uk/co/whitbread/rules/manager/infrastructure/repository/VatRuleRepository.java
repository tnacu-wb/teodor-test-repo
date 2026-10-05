package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.VatRuleEntity;

@Repository
public interface VatRuleRepository extends JpaRepository<VatRuleEntity, Integer> {

  List<VatRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<VatRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status,
      LocalDateTime date);

  List<VatRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
      String status,
      LocalDateTime date);

}
