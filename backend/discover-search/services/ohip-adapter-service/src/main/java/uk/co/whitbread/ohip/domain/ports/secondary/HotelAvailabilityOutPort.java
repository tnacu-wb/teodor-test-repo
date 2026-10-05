package uk.co.whitbread.ohip.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteriaV2;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityRoomSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequestV2;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomMatrix;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomPriceBreakdown;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelAvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventoryResponse;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.domain.model.availability.out.StatisticsDateItem;

public interface HotelAvailabilityOutPort {

  AvailabilityResult getHotelAvailability(AvailabilityRequest availabilityRequest);

  AvailabilityByIdsResult getHotelAvailabilityByIds(
      AvailabilityByIdsSearchRequest availabilitySearch);

  AvailabilityRoomPriceBreakdown getHotelRoomPriceBreakdown(
      AvailabilityRoomSearchCriteria roomAvailabilitySearch);

  ItemInventoryResponse getHotelItemsInventory(
      ItemInventoryRequest itemInventoryRequest);

  RateCodePricingResult getRateCodePricing(RateCodeCriteria rateCodeCriteria);

  List<AvailabilityRoomPriceBreakdown> getRatesInfo(List<AvailabilityRoomSearchCriteria> rooms);

  HotelInventoryRoomType getHotelRoomsInventory(HotelInventoryRequest hotelInventoryRequest);

  List<HotelAvailabilityResult> getMultiHotelAvailabilities(MultiHotelAvailabilityRequest availabilityRequest,
                                                            List<RoomMatrix> roomMatrixList);

  List<String> getRoomTypesFromHotelInventory(String hotelId, String arrivalDate, String departureDate,
                                              int roomCountRequested);

  AvailabilityByIdsResultV2 getHotelAvailabilityByIdsV2(
      AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteria);

  MultiAvailabilityResultV2 getMultiHotelAvailabilitiesV2(
      MultiHotelAvailabilityRequestV2 multiHotelAvailabilityRequest);

  List<StatisticsDateItem> getHotelInventoryStatistics(AvailabilityRequest availabilityRequest);

  RestrictionsByDateRangeResult getRestrictionsByDateRange(
      RestrictionsByDateRangeSearchCriteria restrictionsByDateRangeSearchCriteria);
}
