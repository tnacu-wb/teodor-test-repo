package uk.co.whitbread.reservation.domain.ports.secondary;

import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIdsV2;

public interface HotelAvailabilityOutPort {

  HotelAvailabilityByIdsV2 getHotelAvailabilitiesByIdsV2(HotelAvailabilityByIdsV2Request request);

}
