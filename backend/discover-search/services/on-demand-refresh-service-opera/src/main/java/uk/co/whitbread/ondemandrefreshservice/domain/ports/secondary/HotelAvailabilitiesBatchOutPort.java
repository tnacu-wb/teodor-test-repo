package uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;

public interface HotelAvailabilitiesBatchOutPort {

  void persistHotelEntities(final List<HotelEntity> hotelEntities);

}
