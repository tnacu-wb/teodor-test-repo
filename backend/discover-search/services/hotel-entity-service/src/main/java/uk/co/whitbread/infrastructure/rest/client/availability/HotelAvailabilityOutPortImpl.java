package uk.co.whitbread.infrastructure.rest.client.availability;

import static java.util.stream.Collectors.toMap;
import static uk.co.whitbread.domain.constants.HotelEntityConstants.TWIN;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
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
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AvailabilityByIdsResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ClassificationsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanInfoResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlansResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationLightweightResponseDto;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.client.availability.exception.HotelAvailabilityException;
import uk.co.whitbread.infrastructure.rest.client.availability.mapper.HotelAvailabilityMapper;
import uk.co.whitbread.infrastructure.rest.client.availability.mapper.HotelAvailabilityRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.availability.mapper.HotelInventoryMapper;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityResponseDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.RoomDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.RoomRateDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelAvailabilityOutPortImpl implements HotelAvailabilityOutPort {

  private static final Integer MAX_LIMIT_PER_CALL = 40;
  private static final String DISTR_CHANNEL_ID = "DISTR";
  private static final String TRAVELPORT_SUBCHANNEL = "TRAVELPORT";
  private static final String AMADEUS_SUBCHANNEL = "AMADEUS";
  private final HotelAvailabilityRequestMapper requestMapper;
  private final HotelAvailabilityMapper availabilityMapper;
  private final HotelInventoryMapper hotelInventoryMapper;
  private final OhipClient ohipClient;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  @Override
  public HotelAvailability getHotelAvailability(HotelAvailabilityRequest request) {
    log.debug("Entered getHotelAvailability with hotelAvailabilityRequest={}",
        request);
    var ohipRequest = requestMapper.toOhipDto(request);
    var hotelAvailability = ohipClient.getHotelAvailability(ohipRequest);
    return availabilityMapper.toModel(hotelAvailability);
  }

  @Override
  public HotelAvailabilityByIds getHotelAvailabilityByIds(HotelAvailabilityByIdsRequest request) {
    log.debug("Entered getHotelAvailabilitiesByIds with hotelAvailabilitiesByIdsRequest={}",
        request);
    var ohipRequest = requestMapper.toAvailabilityByIdsOhipModel(request);
    var hotelAvailabilityByIds = ohipClient.getHotelAvailabilityByIds(ohipRequest);

    setHotelAvailabilityByIdsDisplaySets(hotelAvailabilityByIds);

    if (isTwinFilteringNeeded(request)
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFilterTwinOnPriority())) {
      filterTwinRooms(hotelAvailabilityByIds);
    }

    return availabilityMapper.toAvailabilityByIdsModel(hotelAvailabilityByIds);
  }

  private void filterTwinRooms(HotelAvailabilityByIdsDto hotelAvailabilityByIds) {
    // Filter twin rooms to get the highest priority room per specialRequest
    hotelAvailabilityByIds.getHotelAvailability().stream()
        .map(HotelAvailabilityResponseDto::getRoomRates).flatMap(List::stream)
        .map(RoomRateDto::getRoomTypes).flatMap(List::stream)
        .filter(roomTypeDto -> StringUtils.equalsIgnoreCase(roomTypeDto.getRoomType(), TWIN))
        .forEach(roomTypeDto -> {
          List<RoomDto> filteredRooms = roomTypeDto.getRooms().stream()
              .filter(distinctByKey(RoomDto::getSpecialRequests)).toList();

          if (!filteredRooms.isEmpty()) {
            roomTypeDto.setRooms(filteredRooms);
          }
        });
  }

  private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  private boolean isTwinFilteringNeeded(HotelAvailabilityByIdsRequest request) {
    boolean isTwinRoomTypeRequested = request.getRoomTypes().stream()
        .anyMatch(roomType -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(TWIN, roomType));

    boolean isGdsRequest = request.getChannel().equalsIgnoreCase(DISTR_CHANNEL_ID)
        && (request.getSubchannel().equalsIgnoreCase(TRAVELPORT_SUBCHANNEL)
            || request.getSubchannel().equalsIgnoreCase(AMADEUS_SUBCHANNEL));

    return isTwinRoomTypeRequested && isGdsRequest;
  }

  private void setHotelAvailabilityByIdsDisplaySets(
      HotelAvailabilityByIdsDto hotelAvailabilityByIdsDto) {

    // Set for each each ratePlan the corresponding rateDisplaySet
    Flux.fromIterable(hotelAvailabilityByIdsDto.getHotelAvailability())
        .flatMap(
            hotelAvailabilityDto -> ohipClient.getRatePlans(getRatePlansString(hotelAvailabilityDto),
                hotelAvailabilityDto.getHotelId()))
        .collectList()
        .map(ratePlansResponseDtos -> {
          hotelAvailabilityByIdsDto.getHotelAvailability()
              .forEach(
                  hotelAvailabilityDto -> hotelAvailabilityDto.getRoomRates()
                      .forEach(roomRateDto -> roomRateDto.setRateDisplaySet(
                          getDisplaySetsByRatePlans(ratePlansResponseDtos, hotelAvailabilityDto)
                              .getOrDefault(roomRateDto.getRatePlanCode(), new ClassificationsDto()).getDisplaySet()))
              );
          return hotelAvailabilityByIdsDto;
        }).block();

  }

  private Map<String, ClassificationsDto> getDisplaySetsByRatePlans(
      List<RatePlansResponseDto> ratePlansResponseDto, HotelAvailabilityResponseDto hotelAvailabilityDto) {

    return ratePlansResponseDto.stream()
        .map(RatePlansResponseDto::getRatePlans)
        .flatMap(List::stream)
        .filter(ratePlanDto -> ratePlanDto.getHotelId().equals(hotelAvailabilityDto.getHotelId()))
        .collect(toMap(RatePlanDto::getRatePlanCode, RatePlanDto::getClassifications));

  }

  private List<String> getRatePlansString(HotelAvailabilityResponseDto hotelAvailabilityDto) {
    return hotelAvailabilityDto.getRoomRates().stream()
        .map(RoomRateDto::getRatePlanCode).toList();
  }

  @Override
  public Mono<RoomPriceBreakdownResult> getHotelMultiRoomsPriceBreakdown(String hotelId,
      RoomPriceBreakdownRequest roomPriceBreakdownRequest) {
    log.debug("Entered getHotelMultiRoomsPriceBreakdown with roomPriceBreakdownRequest={}",
        roomPriceBreakdownRequest);
    return ohipClient.getRoomPriceBreakdown(hotelId, roomPriceBreakdownRequest);
  }

  @Override
  public RateCodePricingResult getRateCodePricing(RateCodeCriteria rateCodeCriteria) {
    log.debug("Entered getRateCodePricing with rateCodeCriteria={}",
        rateCodeCriteria);
    return ohipClient.getRateCodePricing(rateCodeCriteria);
  }

  @Override
  public HotelInventoryRoomType getHotelRoomsInventory(
      HotelInventoryRequest hotelInventoryRequest) {
    var hotelInventoryRequestOhipDto =
        hotelInventoryMapper.toOhipDto(hotelInventoryRequest);
    return hotelInventoryMapper.toDomainModel(
        ohipClient.getHotelRoomsInventory(hotelInventoryRequestOhipDto));
  }

  @Override
  public MultiAvailabilityResponse getMultiHotelAvailability(
      MultiHotelAvaSearchCriteria multiHotelAvaSearchCriteria) {
    log.debug("Entered getMultiHotelAvailability with multiHotelAvaSearchCriteria={}",
        multiHotelAvaSearchCriteria);
    return availabilityMapper.toMultiAvaDomainModel(
        ohipClient.getMultiHotelAvailability(
            requestMapper.toMultiAvaOhipModel(multiHotelAvaSearchCriteria)));
  }

  @Override
  public HotelAvailabilitiesResponse getMultiHotelAvailabilityWithMigrationStatus(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<HotelMigrationStatusResponse> hotelsMigrationStatus,
      String companyId) {
    log.debug(
        "Entered getMultiHotelAvailabilityWithMigrationStatus with hotelAvailabilitiesRequest={}",
        hotelAvailabilitiesRequest);
    var multiHotelAvaSearch = requestMapper.toMultiAvaSearch(hotelAvailabilitiesRequest);
    // todo: when ready update the upstream request model with dynamic channel
    // when the HotelAvailabilitiesRequest is ready to include channel
    multiHotelAvaSearch.setChannel("CCUI");
    multiHotelAvaSearch.setHotelIds(hotelsMigrationStatus.stream()
        .map(HotelMigrationStatusResponse::getHotelId).toList());
    if (companyId != null) {
      multiHotelAvaSearch.setCompanyId(companyId);
    }
    var multiHotelAvaResponse = ohipClient.getMultiHotelAvailability(multiHotelAvaSearch);
    var hotelAvailabilityResponse = availabilityMapper
        .toHotelAvaResponse(multiHotelAvaResponse);
    hotelAvailabilityResponse.getHotelAvailabilityList().forEach(hotelAvailability ->
        hotelAvailability.setPmsSource(mapPmsSource(hotelAvailability, hotelsMigrationStatus)));
    return hotelAvailabilityResponse;
  }

  @Override
  public HotelAvailabilitiesResponse getMultiHotelAvailabilityWithNewOperaEndpoint(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest, List<HotelMigrationStatusResponse> hotelsMigrationStatus) {
    log.debug(
        "Entered getMultiHotelAvailabilityWithNewOperaEndpoint with hotelAvailabilitiesRequest={}",
        hotelAvailabilitiesRequest);
    var newMultiHotelAvaSearch = requestMapper.toNewMultiAvaSearch(hotelAvailabilitiesRequest);
    newMultiHotelAvaSearch.setHotelIds(hotelsMigrationStatus.stream()
        .map(HotelMigrationStatusResponse::getHotelId).toList());
    newMultiHotelAvaSearch.setIncludePublicRates(true);
    newMultiHotelAvaSearch.setSortBy(hotelAvailabilitiesRequest.getSort());
    newMultiHotelAvaSearch.setLimit(MAX_LIMIT_PER_CALL);
    newMultiHotelAvaSearch.setOffset(0);
    var multiHotelAvaResponse = ohipClient.getPostMultiHotelAvailability(newMultiHotelAvaSearch);
    var hotelAvailabilityResponse = availabilityMapper
        .toNewHotelAvaResponse(multiHotelAvaResponse);
    hotelAvailabilityResponse.getHotelAvailabilityList().forEach(hotelAvailability ->
        hotelAvailability.setPmsSource(mapPmsSource(hotelAvailability, hotelsMigrationStatus)));
    return hotelAvailabilityResponse;
  }

  @Override
  public HotelAvailabilityByIdsV2 getHotelAvailabilityByIdsV2(
      HotelAvailabilityByIdsV2Request request) {
    log.debug("Entered getHotelAvailabilityByIdsV2 with hotelAvailabilitiesByIdsV2Request={}",
        request);
    AvailabilityByIdsResponseV2Dto hotelAvailabilityByIds;
    if (Objects.nonNull(request.getRates())
        && CollectionUtils.isNotEmpty(request.getRates().getCorporateRates())
        && request.getRates().getCorporateRates().size() > 1) {
      var ohipV3Request = requestMapper.toAvailabilityByIdsOhipV3Model(request);
      hotelAvailabilityByIds = ohipClient.getHotelAvailabilityByIdsV3(ohipV3Request);
    } else {
      var ohipRequest = requestMapper.toAvailabilityByIdsOhipV2Model(request);
      hotelAvailabilityByIds = ohipClient.getHotelAvailabilityByIdsV2(ohipRequest);
    }
    return availabilityMapper.toAvailabilityByIdsV2Model(hotelAvailabilityByIds);
  }

  @Override
  public RestrictionsByDateRangeResult getRestrictionsByDateRange(
        RestrictionsByDateRangeRequest restrictionsByDateRangeRequest) {
    return ohipClient.getRestrictionsByDateRange(restrictionsByDateRangeRequest);
  }

  @Override
  public List<RestrictionsByDateRangeResult> getMultiHotelRestrictionsByDateRange(
      MultiHotelRestrictionsByDateRangeRequest multiHotelRestrictionsByDateRangeRequest) {
    return ohipClient.getMultiHotelRestrictionsByDateRange(multiHotelRestrictionsByDateRangeRequest);
  }

  @Override
  public ReservationLightweightResponseDto getLightweightReservations(String hotelId, Set<String> reservationIds) {
    return ohipClient.getLightweightReservations(hotelId, reservationIds).block();
  }

  @Override
  public RatePlanInfoResponseDto getRatePlanInfo(String ratePlanCode, String hotelId) {
    return ohipClient.getRatePlanInfo(ratePlanCode, hotelId);
  }

  private String mapPmsSource(HotelAvailabilityResponse hotelAvailabilityResponse,
                              List<HotelMigrationStatusResponse> hotelsMigrationStatus) {
    return hotelsMigrationStatus.stream().filter(hotelMigration ->
            hotelMigration.getHotelId().equals(hotelAvailabilityResponse.getHotelId()))
        .findFirst().orElseThrow(() -> {
          var message = String.format("No hotel was found with hotelId = %s",
                  hotelAvailabilityResponse.getHotelId());
          var exception = new HotelAvailabilityException(ErrorCode.DIGITAL_NO_HOTEL_FOUND_EXCEPTION,
                  message);
          ExceptionLogger.log(log, exception);
          return exception;
        }).getPmsSource();
  }
}
