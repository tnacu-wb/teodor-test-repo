package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.LocationPriceEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.LocationPriceId;

@Repository
public interface LocationPriceJpaRepository
    extends JpaRepository<LocationPriceEntity, LocationPriceId> {

}
