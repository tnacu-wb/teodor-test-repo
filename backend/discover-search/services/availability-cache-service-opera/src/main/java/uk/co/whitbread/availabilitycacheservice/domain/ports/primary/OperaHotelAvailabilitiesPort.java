package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;

public interface OperaHotelAvailabilitiesPort {

  List<Hotel> getOperaHotelAvailabilities(
      final OperaHotelsSearchCriteria searchCriteria);
}
