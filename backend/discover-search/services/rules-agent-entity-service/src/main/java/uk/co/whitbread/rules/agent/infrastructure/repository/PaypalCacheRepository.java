package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.PaypalRuleEntity;

@Repository
public interface PaypalCacheRepository extends JpaRepository<PaypalRuleEntity, Integer> {

  @Query("SELECT u FROM PaypalRuleEntity u WHERE u.status = 'ACTIVE'")
  List<PaypalRuleEntity> findAllByStatusActive();

  @Query("SELECT u FROM PaypalRuleEntity u WHERE u.lastModifiedAt >= ?1 "
      + "and (u.status='ACTIVE' or u.status='INACTIVE')")
  List<PaypalRuleEntity> findAllUpdatedAfter(LocalDateTime after);
}
