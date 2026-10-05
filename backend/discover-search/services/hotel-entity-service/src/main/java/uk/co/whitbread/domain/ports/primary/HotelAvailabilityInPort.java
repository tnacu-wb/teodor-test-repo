package uk.co.whitbread.domain.ports.primary;

import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.domain.model.availability.out.RateCodePricingResult;

public interface HotelAvailabilityInPort {

  HotelAvailability getHotelAvailability(HotelAvailabilityRequest hotelAvailabilityRequest);

  HotelAvailabilityByIds getHotelAvailabilityByIds(HotelAvailabilityByIdsRequest request);

  RateCodePricingResult getRateCodePricing(RateCodeCriteria rateCodeCriteria);

  HotelInventoryRoomType getHotelRoomsInventory(HotelInventoryRequest hotelInventoryRequest);

  HotelAvailabilityByIdsV2 getHotelAvailabilityByIdsV2(HotelAvailabilityByIdsV2Request request);
}
