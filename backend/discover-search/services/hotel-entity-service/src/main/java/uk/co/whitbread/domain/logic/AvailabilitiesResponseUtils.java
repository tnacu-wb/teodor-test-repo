package uk.co.whitbread.domain.logic;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.srp.in.FacilityFilter;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.ParkingFilter;
import uk.co.whitbread.domain.model.srp.in.RadiusUnitEnum;
import uk.co.whitbread.domain.model.srp.in.RecommendedSearchModifiers;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.model.srp.out.HotelsWithFilterModel;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;

@Slf4j
@UtilityClass
public class AvailabilitiesResponseUtils {

  private static final String MILES = "mi";
  private static final String KILOMETERS = "km";

  private static final List<String> FAMILY_TWIN_ROOMS = List.of("TWIN", "FAM");

  private static final String PMS_SOURCE_OPERA = "OPERA";

  static final Set<String> PARKING_FILTERS =
      Arrays.stream(ParkingFilter.values()).map(ParkingFilter::getValue).collect(Collectors.toSet());

  static final Map<String, List<String>> COMPLEMENTARY_FILTERS = Map.ofEntries(
      Map.entry(FacilityFilter.ACO.getValue(), List.of(FacilityFilter.HAC.getValue())),
      Map.entry(FacilityFilter.HAC.getValue(), List.of(FacilityFilter.ACO.getValue())),
      Map.entry(FacilityFilter.LFT.getValue(), List.of(FacilityFilter.HUL.getValue())),
      Map.entry(FacilityFilter.HUL.getValue(), List.of(FacilityFilter.LFT.getValue())),
      Map.entry(FacilityFilter.RES.getValue(), List.of(FacilityFilter.DIN.getValue(), FacilityFilter.HRS.getValue())),
      Map.entry(FacilityFilter.DIN.getValue(), List.of(FacilityFilter.RES.getValue(), FacilityFilter.HRS.getValue())),
      Map.entry(FacilityFilter.HRS.getValue(), List.of(FacilityFilter.RES.getValue(), FacilityFilter.DIN.getValue())),
      Map.entry(FacilityFilter.HLG.getValue(), List.of(FacilityFilter.LUG.getValue())),
      Map.entry(FacilityFilter.LUG.getValue(), List.of(FacilityFilter.HLG.getValue())),
      Map.entry(FacilityFilter.HAR.getValue(), List.of(FacilityFilter.WET.getValue())),
      Map.entry(FacilityFilter.WET.getValue(), List.of(FacilityFilter.HAR.getValue()))
  );

  static List<String> getComplementaryFilters(String filter) {
    return COMPLEMENTARY_FILTERS.getOrDefault(filter, List.of());
  }


  public static HotelAvailabilitiesResponse buildEmptyResponse(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return HotelAvailabilitiesResponse.builder().hotelAvailabilityList(new ArrayList<>()).total(0)
        .page(hotelAvailabilitiesRequest.getPage() == 1 ? hotelAvailabilitiesRequest.getPageSize()
            : hotelAvailabilitiesRequest.getLazyLoadPageSize())
        .pageSize(hotelAvailabilitiesRequest.getPageSize()).build();
  }

  public static String buildRadiusUnit(RadiusUnitEnum radiusUnitEnum) {
    return switch (radiusUnitEnum) {
      case MILES -> MILES;
      case KILOMETERS -> KILOMETERS;
    };
  }

  /**
   * Return a list with a substitution list for every roomType Used linkedList to be able to keep
   * the elements in the correct order using index to add them.
   */

  public static void updateAvCacheWithSnowdrop(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      HotelAvailabilitiesResponse avCacheResponse,
      List<DistanceFromSearchResponse> snowdropHotels) {

    snowdropHotels.forEach(hotel -> convertDistance(hotel,
        AvailabilitiesResponseUtils.buildRadiusUnit(hotelAvailabilitiesRequest.getRadiusUnit())));

    for (HotelAvailabilityResponse avCacheAvailability : avCacheResponse.getHotelAvailabilityList()) {
      for (DistanceFromSearchResponse snowdropHotel : snowdropHotels) {
        if (avCacheAvailability.getHotelId().equals(snowdropHotel.getHotelId())) {
          //update distance
          avCacheAvailability.setDistance(Double.valueOf(snowdropHotel.getDistance()));
          //update name
          avCacheAvailability.setName(snowdropHotel.getName());
          //update unit
          avCacheAvailability.setUnit(hotelAvailabilitiesRequest.getRadiusUnit().name());
        }
      }
    }
  }

  public static void convertDistance(DistanceFromSearchResponse snowdropHotel, String radiusUnit) {
    int distance = Integer.parseInt(snowdropHotel.getDistance());
    double convertedDistance = DistanceConverter.convertDistance(distance, radiusUnit);
    snowdropHotel.setDistance(Double.toString(convertedDistance));
  }

  public static List<HotelAvailabilityResponse> sanitizeResponseForBb(
      List<HotelAvailabilitiesResponse> responses) {
    log.info("Entered sanitizeResponseForBB with responses {}", responses);
    List<HotelAvailabilityResponse> allResponses = new ArrayList<>();
    MultiValueMap<String, HotelAvailabilityResponse> availabilityMultimap =
        new LinkedMultiValueMap<>();

    responses.forEach(source -> source.getHotelAvailabilityList()
        .forEach(hotel -> availabilityMultimap.add(hotel.getHotelId(), hotel)));

    availabilityMultimap.keySet().forEach(key -> {
      var hotelList = availabilityMultimap.get(key);
      if (hotelList.size() > 1) {
        hotelList.stream().sorted(Comparator.nullsLast(
            Comparator.comparing(HotelAvailabilityResponse::getLowestRoomRate)));
      }
      allResponses.add(hotelList.get(0));
    });

    log.info("Exiting sanitizeResponseForBB with allResponses {}", allResponses);

    return allResponses;
  }

  public static void flagHubHotelsAndUpdateForFamilyOrTwin(List<String> roomTypes,
                                                           HotelAvailabilitiesResponse response,
                                                           List<String> hubHotels) {

    var intersectionResult = roomTypes.stream().distinct().filter(FAMILY_TWIN_ROOMS::contains)
        .collect(Collectors.toSet());

    response.getHotelAvailabilityList().stream()
        .filter(hotelAvailability -> hubHotels.contains(hotelAvailability.getHotelId()))
        .forEach(hotelAvailability -> {
          hotelAvailability.setIsHub(Boolean.TRUE);
          if (!intersectionResult.isEmpty()) {
            hotelAvailability.setAvailable(Boolean.FALSE);
            hotelAvailability.setLimitedAvailability(Boolean.FALSE);
            hotelAvailability.setLowestRoomRate(null);
          }
        });
  }

  public static Boolean checkNumberOfNightsCcui(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    int numberOfNights = getNumberOfNights(hotelAvailabilitiesRequest);
    return (hotelAvailabilitiesRequest.getRoomTypes().size() > 4 || numberOfNights > 9);
  }

  public static int getNumberOfNights(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return (int) Duration.between(
        LocalDate.parse(hotelAvailabilitiesRequest.getArrivalDate()).atStartOfDay(ZoneOffset.UTC),
        LocalDate.parse(hotelAvailabilitiesRequest.getDepartureDate()).atStartOfDay(ZoneOffset.UTC)).toDays();
  }

  public static int getNumberOfNights(HotelAvailabilityRequest hotelAvailabilityRequest) {
    if (Objects.isNull(hotelAvailabilityRequest)
        || StringUtils.isBlank(hotelAvailabilityRequest.getArrivalDate())
        || StringUtils.isBlank(hotelAvailabilityRequest.getDepartureDate())) {
      return 0;
    }
    return (int) Duration.between(
        LocalDate.parse(hotelAvailabilityRequest.getArrivalDate()).atStartOfDay(ZoneOffset.UTC),
        LocalDate.parse(hotelAvailabilityRequest.getDepartureDate()).atStartOfDay(ZoneOffset.UTC)).toDays();
  }

  public static void applySorting(HotelAvailabilitiesResponse hotelAvailabilitiesResponse,
      List<String> sortingOptions) {
    HotelAvailabilitySorter.sort(hotelAvailabilitiesResponse, sortingOptions, null);
  }

  public static void applySorting(HotelAvailabilitiesResponse hotelAvailabilitiesResponse,
      List<String> sortingOptions, RecommendedSearchModifiers recommendedSearchModifiers) {
    HotelAvailabilitySorter.sort(hotelAvailabilitiesResponse, sortingOptions, recommendedSearchModifiers);
  }

  /**
   * This is applying the hotel facility filters on the AvailabilityCache response. The filters might be empty and this
   * is the place where the response of available hotels can be empty. Therefore, a safeguard has been introduced to
   * trigger the {@linkplain ContentServiceOutPort#triggerHotelFacilityFilterUpdate()}.
   *
   * @param cacheSearchOutPort cacheServiceOutPort
   * @param contentServiceOutPort contentServiceOutPort
   * @param hotelAvailabilitiesRequest hotel availability request
   * @param avCacheResponse            the response of Availability Cache
   */
  public static void applyFilters(CacheSearchOutPort cacheSearchOutPort,
      ContentServiceOutPort contentServiceOutPort,
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      HotelAvailabilitiesResponse avCacheResponse) {

    var filters = hotelAvailabilitiesRequest.getFilters();
    if (CollectionUtils.isEmpty(filters)) {
      return;
    }

    var requestFilter =
        hotelAvailabilitiesRequest.getFilters().stream().filter(Objects::nonNull).findFirst()
            .orElse(null);
    if (requestFilter != null && !cacheSearchOutPort.checkCacheFacilitiesFilterAvailability(
        requestFilter)) {
      log.info("Cache not available, triggering HotelFacilitiesFilter update");
      contentServiceOutPort.triggerHotelFacilityFilterUpdate();
      // Cache Update trigger is Async, so we can return as cache is still empty while
      // the code below this block runs. This means that the user will get unfiltered results.
      return;
    }

    var filtersSet = new HashSet<>(filters);
    applyParkingFilters(cacheSearchOutPort, avCacheResponse, filtersSet);
    filtersSet.forEach(filter -> {
      HotelsWithFilterModel filteredHotelList = cacheSearchOutPort.getHotelIdsByFilter(filter);
      Set<String> allFilteredHotelIds = new HashSet<>(
          filteredHotelList != null && filteredHotelList.getHotelIds() != null
              ? filteredHotelList.getHotelIds() : List.of());

      getComplementaryFilters(filter).forEach(complementaryFilter -> {
        HotelsWithFilterModel complementaryHotelList = cacheSearchOutPort.getHotelIdsByFilter(complementaryFilter);
        if (complementaryHotelList != null && complementaryHotelList.getHotelIds() != null) {
          allFilteredHotelIds.addAll(complementaryHotelList.getHotelIds());
        }
      });

      if (!allFilteredHotelIds.isEmpty()) {
        var filteredList = avCacheResponse.getHotelAvailabilityList().stream()
            .filter(hotel -> allFilteredHotelIds.contains(hotel.getHotelId())).toList();

        avCacheResponse.setHotelAvailabilityList(filteredList);
        log.debug("Added filtered hotels for filter={}", filter);
      }
    });
    avCacheResponse.setTotal(avCacheResponse.getHotelAvailabilityList().size());
  }

  private static void applyParkingFilters(CacheSearchOutPort cacheSearchOutPort,
      HotelAvailabilitiesResponse avCacheResponse, Set<String> filtersSet) {

    Set<String> parkingFilters = new HashSet<>();
    Iterator<String> filtersIterator = filtersSet.iterator();
    while (filtersIterator.hasNext()) {
      String filter = filtersIterator.next();
      if (PARKING_FILTERS.contains(filter)) {
        parkingFilters.add(filter);
        filtersIterator.remove();
      }
    }
    if (!parkingFilters.isEmpty()) {
      List<HotelAvailabilityResponse> availableHotelsWithParking = new ArrayList<>();
      parkingFilters.forEach(filter -> {
        HotelsWithFilterModel filteredHotelList = cacheSearchOutPort.getHotelIdsByFilter(filter);
        List<String> filteredHotelIds = filteredHotelList.getHotelIds();
        if (!filteredHotelIds.isEmpty()) {
          availableHotelsWithParking.addAll(avCacheResponse.getHotelAvailabilityList().stream()
              .filter(hotel -> filteredHotelIds.contains(hotel.getHotelId())).toList());
        }
      });
      avCacheResponse.setHotelAvailabilityList(availableHotelsWithParking);
      log.debug("Found {} hotels with parking filters {}", availableHotelsWithParking.size(), parkingFilters);
    }
  }

  /**
   * Getting all the holtelIds with OpeningSoon.
   *
   * <p>It also checks whether the Opening Soon Availability Cache is available.
   * If not, triggers an update to content service on this cache.
   *
   * @param cacheSearchOutPort cacheServiceOutPort
   * @param contentServiceOutPort contentServiceOutPort
   *
   * @return list of openingSoonHotelIds
   */
  public static List<String> getOpeningSoonHotelIds(CacheSearchOutPort cacheSearchOutPort,
      ContentServiceOutPort contentServiceOutPort) {
    if (!cacheSearchOutPort.checkCacheOpeningSoonAvailability()) {
      contentServiceOutPort.triggerHotelsOpeningSoonCacheUpdate();
      return List.of();
    }

    var filteredHotelList = cacheSearchOutPort.getHotelIdsOpeningSoon();
    return filteredHotelList == null ? List.of() : filteredHotelList.getHotelIds();
  }

  public static void applyPagination(HotelAvailabilitiesResponse hotelAvailabilitiesResponse,
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    List<HotelAvailabilityResponse> hotelAvailabilityList = new ArrayList<>();
    Integer startsFrom = getStartingIndex(hotelAvailabilitiesRequest);
    Integer endsTo = getEndingIndex(hotelAvailabilitiesRequest, hotelAvailabilitiesResponse);

    for (int i = startsFrom; i < endsTo; i++) {
      if (i < hotelAvailabilitiesResponse.getHotelAvailabilityList().size()
          && hotelAvailabilitiesResponse.getHotelAvailabilityList().get(i) != null) {
        hotelAvailabilityList.add(hotelAvailabilitiesResponse.getHotelAvailabilityList().get(i));
      }
    }

    hotelAvailabilitiesResponse.setHotelAvailabilityList(hotelAvailabilityList);
    hotelAvailabilitiesResponse.setPage(hotelAvailabilitiesRequest.getPage());
    hotelAvailabilitiesResponse.setPageSize(
        hotelAvailabilitiesRequest.getPage() == 1 ? hotelAvailabilitiesRequest.getPageSize()
            : hotelAvailabilitiesRequest.getLazyLoadPageSize());
  }

  public static Integer getStartingIndex(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    if (hotelAvailabilitiesRequest.getPage() == 1) {
      return 0;
    } else {
      return hotelAvailabilitiesRequest.getPageSize() + (hotelAvailabilitiesRequest.getPage() - 2)
          * hotelAvailabilitiesRequest.getLazyLoadPageSize();
    }
  }

  public static Integer getEndingIndex(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      HotelAvailabilitiesResponse hotelAvailabilitiesResponse) {
    Integer page = hotelAvailabilitiesRequest.getPage();
    Integer pageSize = hotelAvailabilitiesRequest.getPageSize();
    Integer lazyLoadPageSize = hotelAvailabilitiesRequest.getLazyLoadPageSize();
    Integer total = hotelAvailabilitiesResponse.getTotal();

    if (total < (pageSize + (page - 1) * lazyLoadPageSize)) {
      return total;
    } else {
      return pageSize + (page - 1) * lazyLoadPageSize;
    }
  }

  public void updateAvailabilitiesResponseWithOperaSoldOutHotels(
      HotelAvailabilitiesResponse availabilityResponse,
      List<HotelMigrationStatusResponse> operaHotels) {

    Set<String> availableHotelsIds = availabilityResponse.getHotelAvailabilityList().stream()
        .map(HotelAvailabilityResponse::getHotelId).collect(Collectors.toSet());
    var missingOperaHotels = new ArrayList<>(operaHotels);
    missingOperaHotels.removeIf(operaHotel -> availableHotelsIds.contains(operaHotel.getHotelId()));
    if (missingOperaHotels.isEmpty()) {
      return;
    }

    List<HotelAvailabilityResponse> availabilityForMissingHotels =
        buildOperaSoldOutAvailabilityResponses(missingOperaHotels);

    var concatenatedOperaHotels = Stream.concat(availabilityResponse.getHotelAvailabilityList().stream(),
        availabilityForMissingHotels.stream()).toList();
    availabilityResponse.setHotelAvailabilityList(concatenatedOperaHotels);
  }

  private List<HotelAvailabilityResponse> buildOperaSoldOutAvailabilityResponses(
      List<HotelMigrationStatusResponse> operaHotels) {

    return operaHotels.stream()
        .map(
            operaHotel -> HotelAvailabilityResponse.builder()
                .hotelId(operaHotel.getHotelId())
                .available(Boolean.FALSE).limitedAvailability(Boolean.FALSE)
                .pmsSource(PMS_SOURCE_OPERA)
                .build())
        .toList();
  }

  public static void applyOccupancySupplement(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      HotelAvailabilitiesResponse avCacheResponse, UnleashWrapper<FeatureFlag> unleashWrapper,
      RulesAgentOutPort rulesAgentOutPort) {

    var noOfExtraAdults = hotelAvailabilitiesRequest.getAdultsNumber().stream()
        .filter(number -> number.intValue() > 1)
        .count();


    if (noOfExtraAdults > 0 && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getOccupancySupplement())) {

      var hotelIds = avCacheResponse.getHotelAvailabilityList().stream()
          .map(HotelAvailabilityResponse::getHotelId).toList();
      var occSupplement = rulesAgentOutPort.getMultiOccupancySupplementPricing(hotelIds);

      avCacheResponse.getHotelAvailabilityList()
          .stream()
          .filter(hotel -> Objects.nonNull(hotel.getLowestRoomRate())
              && Objects.nonNull(hotel.getLowestRoomRate().getNetTotal()))
          .forEach(
              hotel -> hotel.getLowestRoomRate()
                  .setNetTotal(hotel.getLowestRoomRate().getNetTotal()
                      .add((occSupplement.get(hotel.getHotelId()))
                          .multiply(BigDecimal.valueOf(noOfExtraAdults))
                          .multiply(BigDecimal.valueOf(getNumberOfNights(hotelAvailabilitiesRequest))))));
    }
  }
}
