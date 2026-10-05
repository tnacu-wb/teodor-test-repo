package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomsRuleEntity;

@Repository
public interface MaxRoomsRepository extends JpaRepository<MaxRoomsRuleEntity, Integer> {

  List<MaxRoomsRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<MaxRoomsRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(String status,
      LocalDateTime date);

  List<MaxRoomsRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(String status,
      LocalDateTime date);

}
