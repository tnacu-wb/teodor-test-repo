package uk.co.whitbread.domain.ports.primary;

import uk.co.whitbread.domain.model.availabilitycache.in.AvailabilityCacheSearchCriteria;
import uk.co.whitbread.domain.model.availabilitycache.out.HotelAvailabilitiesResponse;

public interface AvailabilityCacheSearchInPort {

  HotelAvailabilitiesResponse getAvailabilitiesFromAvailabilityCache(
      AvailabilityCacheSearchCriteria searchCriteria);

}
