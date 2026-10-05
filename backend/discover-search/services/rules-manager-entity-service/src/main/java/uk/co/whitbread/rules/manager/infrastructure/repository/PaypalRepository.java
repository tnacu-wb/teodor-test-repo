package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.PaypalRuleEntity;


@Repository
public interface PaypalRepository extends JpaRepository<PaypalRuleEntity, Integer> {

  List<PaypalRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<PaypalRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status, LocalDateTime date);

  List<PaypalRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(String status,
                                                                                   LocalDateTime date);
}
