package uk.co.whitbread.domain.logic;

import static uk.co.whitbread.domain.logic.RulesAgentValidations.hasNotNullArrivalAndDepartureDate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.model.migrationstatus.in.HotelsMigrationStatusRequest;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheV1SearchOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;

@RequiredArgsConstructor
@Slf4j
public class AvailabilitiesResponseFromAvCache {

  private static final String PMS_SOURCE_OPERA = "OP";
  private static final String PI_CHANNEL_ID = "PI";
  private static final String CCUI_CHANNEL_ID = "CCUI";
  private static final String HUB_BRAND = "HUB";
  private final AvailabilityCacheV1SearchOutPort avCaheOutPort;
  private final RulesAgentOutPort rulesAgentOutPort;
  private final OnSaleFlagOutPort onSaleFlagOutPortImpl;
  private final SnowdropInformation snowdropInformation;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()",
      cacheManager = "cacheManager5Minutes", value = "FullHotelAvailabilitiesCache", key =
      "{#hotelAvailabilitiesRequest.location, "
          + "#hotelAvailabilitiesRequest.arrivalDate, #hotelAvailabilitiesRequest.departureDate, "
          + "#hotelAvailabilitiesRequest.roomTypes, "
          + "#hotelAvailabilitiesRequest.ratePlanCodes, #flagMlos}")
  public HotelAvailabilitiesResponse getFullAvailabilitiesFromAvCache(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest, boolean isValidationRequired,
      boolean flagMlos) {
    log.info("hotelAvailabilitiesRequest={}", hotelAvailabilitiesRequest);

    //get hotels from Snowdrop
    List<DistanceFromSearchResponse> snowdropHotelList =
        snowdropInformation.getHotelsFromSnowdrop(hotelAvailabilitiesRequest);

    if (snowdropHotelList.isEmpty()) {
      return AvailabilitiesResponseUtils.buildEmptyResponse(hotelAvailabilitiesRequest);
    }

    final List<String> hubHotels = getHubHotels(snowdropHotelList);

    HotelAvailabilitiesResponse hotelAvailabilitiesResponse;

    if (hasNotNullArrivalAndDepartureDate(hotelAvailabilitiesRequest)) {
      //get availabilities from Av Cache
      hotelAvailabilitiesResponse = avCaheOutPort.getAvailabilitiesFromAvCache(hotelAvailabilitiesRequest,
              snowdropInformation.getSnowdropHotelIds(snowdropHotelList),
              buildAvCacheRoomTypes(hotelAvailabilitiesRequest, buildChannel(isValidationRequired)), flagMlos);


      //update Av Cache response with sold out availabilities not returned by Av Cache
      updateAvCacheWithSoldOutAvailabilities(hotelAvailabilitiesResponse, snowdropHotelList);
    } else {
      //create HotelAvailabilitiesResponse in case of null arrival and departure date
      List<HotelAvailabilityResponse> hotelAvailabilityResponses = buildHotelAvailabilityResponses(snowdropHotelList);
      hotelAvailabilitiesResponse = HotelAvailabilitiesResponse.builder()
              .hotelAvailabilityList(hotelAvailabilityResponses)
              .total(hotelAvailabilityResponses.size()).build();
    }

    //update Av Cache response with distance,unit,name from Snowdrop
    AvailabilitiesResponseUtils.updateAvCacheWithSnowdrop(hotelAvailabilitiesRequest,
        hotelAvailabilitiesResponse, snowdropHotelList);

    //update limitedAvailability = false when available = false
    updateLimitedAvailabilityForSoldOutHotels(hotelAvailabilitiesResponse);

    AvailabilitiesResponseUtils.flagHubHotelsAndUpdateForFamilyOrTwin(
        hotelAvailabilitiesRequest.getRoomTypes(), hotelAvailabilitiesResponse, hubHotels);

    return hotelAvailabilitiesResponse;
  }

  private List<List<String>> buildAvCacheRoomTypes(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest, String channelId) {
    LinkedList<List<String>> roomSubstitutionList = new LinkedList<>();
    List<String> roomSubstitutedTypes = new ArrayList<>();
    for (int i = 0; i < hotelAvailabilitiesRequest.getRoomTypes().size(); i++) {
      roomSubstitutedTypes.add(hotelAvailabilitiesRequest.getRoomTypes().get(i));
      roomSubstitutedTypes.addAll(rulesAgentOutPort.getRoomSubstitutionRule(
              RoomSubstitutionRuleRequest.builder()
                  .adults(hotelAvailabilitiesRequest.getAdultsNumber().get(i))
                  .children((hotelAvailabilitiesRequest.getChildrenNumber().get(i)))
                  .roomType(hotelAvailabilitiesRequest.getRoomTypes().get(i))
                  //TODO: replace with correct value
                  .pms(PMS_SOURCE_OPERA).channel(channelId).build()).getSubstitutionList().stream()
          .map(RoomSubstitution::getType).toList());
      roomSubstitutionList.add(i,
          roomSubstitutedTypes.stream().map(roomType -> roomType.replaceAll("\\s+", "")).toList());
      roomSubstitutedTypes.clear();
    }
    return roomSubstitutionList;
  }

  private String buildChannel(boolean isValidationRequired) {
    if (isValidationRequired) {
      return PI_CHANNEL_ID;
    } else {
      return CCUI_CHANNEL_ID;
    }
  }

  private void updateLimitedAvailabilityForSoldOutHotels(
      HotelAvailabilitiesResponse hotelAvailabilitiesResponse) {
    hotelAvailabilitiesResponse.getHotelAvailabilityList().stream()
        .filter(hotelAvailability -> Boolean.FALSE.equals(hotelAvailability.getAvailable()))
        .forEach(hotelAvailability -> hotelAvailability.setLimitedAvailability(Boolean.FALSE));
  }

  private void updateAvCacheWithSoldOutAvailabilities(HotelAvailabilitiesResponse avCacheResponse,
      List<DistanceFromSearchResponse> snowdropResponse) {

    List<DistanceFromSearchResponse> missingSnowdropHotels = new ArrayList<>(snowdropResponse);

    missingSnowdropHotels.removeIf(
        snowdropHotel -> avCacheResponse.getHotelAvailabilityList().stream().anyMatch(
            availabilityCacheHotel -> snowdropHotel.getHotelId()
                .equals(availabilityCacheHotel.getHotelId())));

    if (missingSnowdropHotels.isEmpty()) {
      return;
    }

    //build missing hotels
    List<HotelAvailabilityResponse> missingAvCacheHotels =
            buildMissingHotels(missingSnowdropHotels);

    var concatenatedAvCacheHotels =
        Stream.concat(avCacheResponse.getHotelAvailabilityList().stream(),
            missingAvCacheHotels.stream()).toList();

    avCacheResponse.setHotelAvailabilityList(concatenatedAvCacheHotels);
    avCacheResponse.setTotal(concatenatedAvCacheHotels.size());
  }

  private List<HotelAvailabilityResponse> buildMissingHotels(
          List<DistanceFromSearchResponse> snowdropHotels) {

    Map<String, String> hotelMigrationStatusMap = buildHotelMigrationStatusMap(snowdropHotels);

    return snowdropHotels.stream()
            .filter(snowdropHotel -> hotelMigrationStatusMap.containsValue(snowdropHotel.getHotelId()))
            .map(
                    snowdropHotel -> HotelAvailabilityResponse.builder().hotelId(snowdropHotel.getHotelId())
                            .available(Boolean.FALSE).limitedAvailability(Boolean.FALSE)
                            .pmsSource(hotelMigrationStatusMap.get(snowdropHotel.getHotelId())).build())
            .toList();
  }

  private List<HotelAvailabilityResponse> buildHotelAvailabilityResponses(
      List<DistanceFromSearchResponse> snowdropHotels) {

    Map<String, String> hotelMigrationStatusMap = buildHotelMigrationStatusMap(snowdropHotels);

    return snowdropHotels.stream()
        .filter(snowdropHotel -> hotelMigrationStatusMap.containsKey(snowdropHotel.getHotelId()))
        .map(
            snowdropHotel -> HotelAvailabilityResponse.builder().hotelId(snowdropHotel.getHotelId())
                .available(Boolean.TRUE).limitedAvailability(Boolean.FALSE)
                .pmsSource(hotelMigrationStatusMap.get(snowdropHotel.getHotelId())).build())
        .toList();
  }

  private Map<String, String> buildHotelMigrationStatusMap(
      List<DistanceFromSearchResponse> snowdropHotels) {

    List<String> snowdropHotelIds =
        snowdropHotels.stream().map(DistanceFromSearchResponse::getHotelId).toList();

    var migrationStatuses = onSaleFlagOutPortImpl.getOnSaleFlag(
        HotelsMigrationStatusRequest.builder().hotelIds(snowdropHotelIds).build());

    return migrationStatuses.getMigrationStatusList().stream()
        .filter(migrationStatus -> migrationStatus.getOnSale().equals(Boolean.TRUE)).collect(
            Collectors.toMap(HotelMigrationStatusResponse::getHotelId,
                HotelMigrationStatusResponse::getPmsSource));
  }

  private List<String> getHubHotels(List<DistanceFromSearchResponse> snowdropHotels) {
    return snowdropHotels.stream().filter(hotel -> HUB_BRAND.equals(hotel.getBrand()))
        .map(DistanceFromSearchResponse::getHotelId).toList();
  }
}
