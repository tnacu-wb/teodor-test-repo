package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.OccupancySupplementEntity;

@Repository
public interface OccupancySupplementRepository extends JpaRepository<OccupancySupplementEntity, Integer> {

  @Query("""
      SELECT u
      FROM OccupancySupplementEntity u
      WHERE u.status = 'ACTIVE'
      """)
  List<OccupancySupplementEntity> findAllByStatusActive();

  @Query("""
      SELECT u
      FROM OccupancySupplementEntity u
      WHERE u.lastModifiedAt >= ?1
      AND (u.status='ACTIVE' or u.status='INACTIVE')
      """)
  List<OccupancySupplementEntity> findAllUpdatedAfter(LocalDateTime lastCacheUpdate);
}
