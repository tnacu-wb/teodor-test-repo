package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.ChannelRuleEntity;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelRuleEntity, Integer> {

  @Query("SELECT u FROM ChannelRuleEntity u WHERE u.status = 'ACTIVE'")
  List<ChannelRuleEntity> findAllByStatusActive();

  @Query("SELECT u FROM ChannelRuleEntity u WHERE u.lastModifiedAt >= ?1 "
      + "and (u.status='ACTIVE' or u.status='INACTIVE')")
  List<ChannelRuleEntity> findAllUpdatedAfter(LocalDateTime after);

}
