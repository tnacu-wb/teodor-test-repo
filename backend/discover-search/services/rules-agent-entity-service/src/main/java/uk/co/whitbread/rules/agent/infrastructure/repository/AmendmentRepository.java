package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.AmendmentRuleEntity;

@Repository
public interface AmendmentRepository extends JpaRepository<AmendmentRuleEntity, Integer> {

  @Query("SELECT u FROM AmendmentRuleEntity u WHERE u.status = 'ACTIVE'")
  List<AmendmentRuleEntity> findAllByStatusActive();

  @Query("SELECT u FROM AmendmentRuleEntity u WHERE u.lastModifiedAt >= ?1 "
      + "and (u.status='ACTIVE' or u.status='INACTIVE')")
  List<AmendmentRuleEntity> findAllUpdatedAfter(LocalDateTime after);

}
