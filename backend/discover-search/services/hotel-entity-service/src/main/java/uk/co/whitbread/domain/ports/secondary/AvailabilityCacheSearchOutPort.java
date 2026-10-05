package uk.co.whitbread.domain.ports.secondary;

import uk.co.whitbread.domain.model.availabilitycache.in.AvailabilityCacheSearchCriteria;
import uk.co.whitbread.domain.model.availabilitycache.out.HotelAvailabilitiesResponse;

public interface AvailabilityCacheSearchOutPort {

  HotelAvailabilitiesResponse getAvailabilitiesFromAvailabilityCache(
      AvailabilityCacheSearchCriteria searchCriteria);

}
