package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.ChannelRuleEntity;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelRuleEntity, Integer> {

  List<ChannelRuleEntity> findAllByStatusEqualsIgnoreCase(String status);

  List<ChannelRuleEntity> findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
      String status, LocalDateTime date);

  List<ChannelRuleEntity> findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
      String status,
      LocalDateTime date);
}
