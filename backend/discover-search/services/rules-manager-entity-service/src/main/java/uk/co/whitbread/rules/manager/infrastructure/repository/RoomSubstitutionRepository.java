package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RoomSubstitutionRuleEntity;

@Repository
public interface RoomSubstitutionRepository extends
    JpaRepository<RoomSubstitutionRuleEntity, Integer> {

  List<RoomSubstitutionRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<RoomSubstitutionRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status,
      LocalDateTime date);

  List<RoomSubstitutionRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
      String status,
      LocalDateTime date);

}
