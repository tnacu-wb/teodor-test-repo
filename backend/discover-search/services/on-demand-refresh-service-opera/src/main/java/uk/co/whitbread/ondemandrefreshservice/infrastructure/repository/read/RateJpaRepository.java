package uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RatePlanEntity;

@Repository
public interface RateJpaRepository extends JpaRepository<RatePlanEntity, String> {

}
