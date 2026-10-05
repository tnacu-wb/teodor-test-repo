package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.BusinessAllowanceRuleEntity;

@Repository
public interface BusinessAllowanceRepository
    extends JpaRepository<BusinessAllowanceRuleEntity, Integer> {

  @Query("SELECT u FROM BusinessAllowanceRuleEntity u WHERE u.status = 'ACTIVE'")
  List<BusinessAllowanceRuleEntity> findAllByStatusActive();

  @Query("SELECT u FROM BusinessAllowanceRuleEntity u WHERE u.lastModifiedAt >= ?1 "
      + "and (u.status='ACTIVE' or u.status='INACTIVE')")
  List<BusinessAllowanceRuleEntity> findAllUpdatedAfter(LocalDateTime after);
}
