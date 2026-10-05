package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.BaseRateRuleEntity;

@Repository
public interface BaseRateCacheRepository extends JpaRepository<BaseRateRuleEntity, Integer> {
  
  @Query("SELECT u FROM BaseRateRuleEntity u WHERE u.status = 'ACTIVE'")
  List<BaseRateRuleEntity> findAllByStatusActive();
  
  @Query("SELECT u FROM BaseRateRuleEntity u WHERE u.lastModifiedAt >= ?1 "
      + "and (u.status='ACTIVE' or u.status='INACTIVE')")
  List<BaseRateRuleEntity> findAllUpdatedAfter(LocalDateTime after);
}
