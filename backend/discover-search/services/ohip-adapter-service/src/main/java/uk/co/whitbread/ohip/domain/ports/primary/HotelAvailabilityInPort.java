package uk.co.whitbread.ohip.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteriaV2;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilitySearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequestV2;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomPriceBreakdownRequest;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventoryResponse;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomPriceBreakdownResult;

public interface HotelAvailabilityInPort {

  AvailabilityResult getHotelAvailability(AvailabilitySearchCriteria availabilitySearch);

  AvailabilityByIdsResult getHotelAvailabilityByIds(
      AvailabilityByIdsSearchCriteria availabilitySearch);

  RoomPriceBreakdownResult getHotelMultiRoomsPriceBreakdown(String hotelId,
      RoomPriceBreakdownRequest roomPriceBreakdownRequest);

  RateCodePricingResult getRateCodePricing(RateCodeCriteria rateCodeCriteria);

  HotelInventoryRoomType getHotelRoomsInventory(HotelInventoryRequest hotelInventoryRequest);

  MultiAvailabilityResult getMultiHotelAvailability(
      MultiHotelAvailabilityRequest availabilityRequest);

  MultiAvailabilityResultV2 getMultiHotelAvailabilityV2(
      MultiHotelAvailabilityRequestV2 multiHotelAvailabilityRequest);

  List<RestrictionsByDateRangeResult> getMultiHotelRestrictionsByDateRange(
      List<RestrictionsByDateRangeSearchCriteria> restrictionsByDateRangeSearchCriteria);

  AvailabilityByIdsResultV2 getHotelAvailabilityByIdsV2(
      AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteria);

  ItemInventoryResponse getHotelItemsInventory(ItemInventoryRequest itemInventoryRequest);

  RestrictionsByDateRangeResult getRestrictionsByDateRange(
      RestrictionsByDateRangeSearchCriteria restrictionsByDateRangeSearchCriteria);
}
