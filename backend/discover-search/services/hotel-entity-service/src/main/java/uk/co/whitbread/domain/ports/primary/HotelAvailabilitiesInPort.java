package uk.co.whitbread.domain.ports.primary;

import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;

public interface HotelAvailabilitiesInPort {

  HotelAvailabilitiesResponse getAvailabilities(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest);
}
