package uk.co.whitbread.avail.business.events.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;

public interface HotelEntityJpaRepository extends JpaRepository<HotelEntity, String> {

}
