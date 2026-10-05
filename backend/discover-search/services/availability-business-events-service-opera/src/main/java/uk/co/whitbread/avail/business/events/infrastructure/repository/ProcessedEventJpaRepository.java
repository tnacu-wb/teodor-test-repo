package uk.co.whitbread.avail.business.events.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.avail.business.events.infrastructure.entity.ProcessedEventEntity;

@Repository
public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventEntity, String> {

}
