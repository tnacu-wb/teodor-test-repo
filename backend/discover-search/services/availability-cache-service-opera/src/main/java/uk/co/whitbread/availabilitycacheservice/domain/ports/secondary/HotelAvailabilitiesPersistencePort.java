package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public interface HotelAvailabilitiesPersistencePort {

  List<Hotel> getHotelsByCodeAndAvailDateBetween(final SearchCriteria searchCriteria);

}
