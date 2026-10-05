package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelMigrationStatusEntity;

public interface HotelMigrationStatusReaderJpaRepository extends
    JpaRepository<HotelMigrationStatusEntity, String> {

  Optional<HotelMigrationStatusEntity> findByHotelCode(String hotelCode);

}
