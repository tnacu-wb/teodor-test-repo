package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RateSuppressionRuleEntity;

@Repository
public interface RateSuppressionCacheRepository extends
    JpaRepository<RateSuppressionRuleEntity, Integer> {


  @Query("SELECT u FROM RateSuppressionRuleEntity u WHERE u.status = 'ACTIVE'")
  List<RateSuppressionRuleEntity> findAllByStatusActive();

  @Query("SELECT u FROM RateSuppressionRuleEntity u WHERE u.lastModifiedAt >= ?1 "
      + "and (u.status='ACTIVE' or u.status='INACTIVE')")
  List<RateSuppressionRuleEntity> findAllUpdatedAfter(LocalDateTime after);

}
