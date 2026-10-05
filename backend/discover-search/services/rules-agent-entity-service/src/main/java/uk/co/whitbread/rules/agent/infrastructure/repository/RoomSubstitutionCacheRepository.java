package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RoomSubstitutionRuleEntity;

@Repository
public interface RoomSubstitutionCacheRepository extends
    JpaRepository<RoomSubstitutionRuleEntity, Integer> {

  @Query("SELECT u FROM RoomSubstitutionRuleEntity u WHERE u.status = 'ACTIVE'")
  List<RoomSubstitutionRuleEntity> findAllByStatusActive();

  @Query("SELECT u FROM RoomSubstitutionRuleEntity u WHERE u.lastModifiedAt >= ?1 "
      + "and (u.status='ACTIVE' or u.status='INACTIVE')")
  List<RoomSubstitutionRuleEntity> findAllUpdatedAfter(LocalDateTime after);

}