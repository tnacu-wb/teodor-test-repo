package uk.co.whitbread.domain.logic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.model.migrationstatus.in.HotelsMigrationStatusRequest;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelsMigrationStatusResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;

@RequiredArgsConstructor
@Slf4j
public class AvailabilitiesResponseFromOpera {

  private static final String PMS_SOURCE_OPERA = "OPERA";
  private static final String HUB_BRAND = "HUB";
  private final SnowdropInformation snowdropInformation;
  private final HotelAvailabilityOutPort ohipAdapterOutPort;
  private final OnSaleFlagOutPort onSaleFlagOutPortImpl;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager5Minutes",
      value = "FullHotelAvailabilitiesFromOpera", key = "{#hotelAvailabilitiesRequest.location, "
      + "#hotelAvailabilitiesRequest.arrivalDate, #hotelAvailabilitiesRequest.departureDate, "
      + "#hotelAvailabilitiesRequest.roomTypes}")
  public HotelAvailabilitiesResponse getFullAvailabilitiesFromOpera(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest, String companyId) {
    //get hotels from Snowdrop
    List<DistanceFromSearchResponse> snowdropHotelList = snowdropInformation.getHotelsFromSnowdrop(
        hotelAvailabilitiesRequest);

    if (snowdropHotelList.isEmpty()) {
      return AvailabilitiesResponseUtils.buildEmptyResponse(hotelAvailabilitiesRequest);
    }

    var hubHotels = getHubHotels(snowdropHotelList);

    //get migration status from Hotel Migration Status
    var migrationStatusResponse = onSaleFlagOutPortImpl.getOnSaleFlag(
        HotelsMigrationStatusRequest.builder()
            .hotelIds(snowdropInformation.getSnowdropHotelIds(snowdropHotelList))
            .build());
    List<HotelMigrationStatusResponse> operaMigratedHotels = getOperaMigratedHotels(
        migrationStatusResponse);

    if (operaMigratedHotels.isEmpty()) {
      return AvailabilitiesResponseUtils.buildEmptyResponse(hotelAvailabilitiesRequest);
    }

    //get availabilities from OHIP Adapter -> OHIP Adapter -> OPERA
    var ohipAdapterResponse = ohipAdapterOutPort
        .getMultiHotelAvailabilityWithMigrationStatus(hotelAvailabilitiesRequest,
            operaMigratedHotels, companyId);

    //update Av Cache response with distance,unit,name from Snowdrop
    AvailabilitiesResponseUtils.updateAvCacheWithSnowdrop(hotelAvailabilitiesRequest,
        ohipAdapterResponse,
        snowdropHotelList);

    AvailabilitiesResponseUtils.flagHubHotelsAndUpdateForFamilyOrTwin(
        hotelAvailabilitiesRequest.getRoomTypes(),
        ohipAdapterResponse, hubHotels);

    return ohipAdapterResponse;
  }

  private List<HotelMigrationStatusResponse> getOperaMigratedHotels(
      HotelsMigrationStatusResponse migrationStatusResponse) {

    var operaMigratedHotels = migrationStatusResponse.getMigrationStatusList()
        .stream()
        .filter(migrationStatus -> PMS_SOURCE_OPERA.equals(migrationStatus.getPmsSource())
            && migrationStatus.getOnSale())
        .toList();

    log.info("The following Opera migrated hotels={} were found", operaMigratedHotels);
    return operaMigratedHotels;
  }

  private List<String> getHubHotels(List<DistanceFromSearchResponse> snowdropHotels) {
    return snowdropHotels.stream()
        .filter(hotel -> HUB_BRAND.equals(hotel.getBrand()))
        .map(DistanceFromSearchResponse::getHotelId).toList();
  }
}
