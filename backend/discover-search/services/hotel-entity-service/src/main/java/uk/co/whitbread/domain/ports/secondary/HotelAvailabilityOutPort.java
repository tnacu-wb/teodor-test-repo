package uk.co.whitbread.domain.ports.secondary;

import java.util.List;
import java.util.Set;
import reactor.core.publisher.Mono;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.domain.model.availability.in.MultiHotelAvaSearchCriteria;
import uk.co.whitbread.domain.model.availability.in.MultiHotelRestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.domain.model.availability.in.RestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.in.RoomPriceBreakdownRequest;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.domain.model.availability.out.MultiAvailabilityResponse;
import uk.co.whitbread.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdownResult;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanInfoResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationLightweightResponseDto;

public interface HotelAvailabilityOutPort {

  HotelAvailability getHotelAvailability(HotelAvailabilityRequest hotelAvailabilityRequest);

  HotelAvailabilityByIds getHotelAvailabilityByIds(HotelAvailabilityByIdsRequest request);

  Mono<RoomPriceBreakdownResult> getHotelMultiRoomsPriceBreakdown(String hotelId,
      RoomPriceBreakdownRequest roomPriceBreakdownRequest);

  RateCodePricingResult getRateCodePricing(RateCodeCriteria rateCodeCriteria);

  HotelInventoryRoomType getHotelRoomsInventory(HotelInventoryRequest hotelInventoryRequest);

  MultiAvailabilityResponse getMultiHotelAvailability(
      MultiHotelAvaSearchCriteria multiHotelAvaSearchCriteria);

  HotelAvailabilitiesResponse getMultiHotelAvailabilityWithMigrationStatus(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<HotelMigrationStatusResponse> hotelsMigrationStatus,
      String companyId);

  HotelAvailabilitiesResponse getMultiHotelAvailabilityWithNewOperaEndpoint(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<HotelMigrationStatusResponse> hotelsMigrationStatus);

  HotelAvailabilityByIdsV2 getHotelAvailabilityByIdsV2(HotelAvailabilityByIdsV2Request request);

  RestrictionsByDateRangeResult getRestrictionsByDateRange(
      RestrictionsByDateRangeRequest restrictionsByDateRangeRequest);

  List<RestrictionsByDateRangeResult> getMultiHotelRestrictionsByDateRange(
      MultiHotelRestrictionsByDateRangeRequest multiHotelRestrictionsByDateRangeRequest);

  ReservationLightweightResponseDto getLightweightReservations(String hotelId,
      Set<String> reservationIds);

  RatePlanInfoResponseDto getRatePlanInfo(String ratePlanCode, String hotelId);
}
