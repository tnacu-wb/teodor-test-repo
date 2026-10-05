package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelLocationEntity;

public interface HotelLocationPersistencePort {

  void update(final List<HotelLocationEntity> hotelLocationEntities);

}
