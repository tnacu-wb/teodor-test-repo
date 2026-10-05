package uk.co.whitbread.avail.business.events.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RatePlanEntity;

public interface RateEntityJpaRepository extends JpaRepository<RatePlanEntity, String> {

}
