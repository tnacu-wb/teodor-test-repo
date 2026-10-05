package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BaseRateRuleEntity;

@Repository
public interface BaseRateRepository extends JpaRepository<BaseRateRuleEntity, Integer> {
  
  List<BaseRateRuleEntity> findAllByStatusEqualsIgnoreCase(String status);
  
  List<BaseRateRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(String status,
                                                                                      LocalDateTime date);
  
  List<BaseRateRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(String status,
                                                                                     LocalDateTime date);
}
