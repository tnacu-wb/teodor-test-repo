package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public interface HotelAvailabilitiesPort {

  List<Hotel> getHotelAvailabilities(final SearchCriteria searchCriteria);
}
