package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelLocationPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelLocationEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelLocationJpaRepository;

@Slf4j
@AllArgsConstructor
public class HotelLocationPersistenceAdapter implements HotelLocationPersistencePort {

  private final HotelLocationJpaRepository hotelLocationJpaRepository;

  public void update(final List<HotelLocationEntity> hotelLocationEntities) {
    try {
      hotelLocationJpaRepository.deleteAllInBatch();
      hotelLocationJpaRepository.saveAll(hotelLocationEntities);
      log.info("HOTEL_LOCATION table successfully updated with {} locations.", hotelLocationEntities.size());
    } catch (Exception ex) {
      log.error("Error while trying to update HOTEL_LOCATION table", ex);
      throw ex;
    }
  }

}
