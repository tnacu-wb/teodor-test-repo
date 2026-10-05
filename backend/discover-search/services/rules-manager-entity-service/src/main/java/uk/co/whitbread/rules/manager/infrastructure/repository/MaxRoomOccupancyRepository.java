package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;

@Repository
public interface MaxRoomOccupancyRepository extends
    JpaRepository<MaxRoomOccupancyRuleEntity, Integer> {

  List<MaxRoomOccupancyRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<MaxRoomOccupancyRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status,
      LocalDateTime date);

  List<MaxRoomOccupancyRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
      String status,
      LocalDateTime date);

}
