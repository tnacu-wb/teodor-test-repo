package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderRate;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop.HotelDetails;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.RulesAgentInPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderLocationAvailabilititesOutPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.SnowdropHotelLookupPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.PriceFinderProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.CalendarPriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;

@Slf4j
@AllArgsConstructor
public class PriceFinderLocationAvailabilitiesOutPortImpl implements PriceFinderLocationAvailabilititesOutPort {

  private PriceFinderAvailabilitiesPersistencePort priceFinderAvailabilitiesPersistencePort;
  private SnowdropHotelLookupPort snowdropHotelLookupPort;
  private PriceFinderAvailabilitiesPostProcessorPort postProcessorPort;
  private PriceFinderProperties properties;
  private final RulesAgentInPort rulesAgentInPort;

  private static final String DB_ROOM_TYPE = "DB";

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager5Minutes",
      value = "LowestPricesByLocation", key = "{#criteria.locationId, #criteria.arrival, #criteria.daysRange}")
  public List<PriceFinderOperaHotelAvailabilities> getAvailabilitiesByLocation(
      PriceFinderLocationSearchCriteria criteria, LocalDate dateRangeEnd) {
    return getHotelAvailabilities(criteria, dateRangeEnd);
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager5Minutes",
      value = "LowestPricesByLocationForCalendar",
      key = "{#calendarCriteria.locationId, #calendarCriteria.milesRadius, #calendarCriteria.month}")
  public CalendarPriceFinderOperaHotelAvailabilities getAvailabilitiesByLocationForCalendar(
      CalendarPriceFinderLocationSearchCriteria calendarCriteria) {
    return getHotelAvailabilitiesForCalendar(calendarCriteria);
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager5Minutes",
      value = "LowestMonthlyPrice", key = "{#criteria.locationId,"
      + " T(java.time.LocalDate).parse(#criteria.arrival).getMonth()}")
  public PriceFinderRate getLowestMonthlyRateByLocation(PriceFinderLocationSearchCriteria criteria) {
    LocalDate arrivalDate = LocalDate.parse(criteria.getArrival());
    LocalDate today = LocalDate.now();

    arrivalDate = isSameMonth(today, arrivalDate) ?  today : arrivalDate.withDayOfMonth(1);
    criteria.setArrival(arrivalDate.toString());
    LocalDate departureDate = arrivalDate.withDayOfMonth(arrivalDate.lengthOfMonth());

    return getHotelAvailabilities(criteria, departureDate).stream()
        .flatMap(hotel -> hotel.getAvailabilities().stream())
        .filter(availabilities -> availabilities.getMinimumRate().compareTo(BigDecimal.ZERO) > 0)
        .map(availabilities -> PriceFinderRate.builder().price(availabilities.getMinimumRate())
            .currency(availabilities.getCurrency()).build())
        .min(Comparator.comparing(PriceFinderRate::getPrice))
        .orElse(PriceFinderRate.builder().price(BigDecimal.ZERO).currency(StringUtils.EMPTY).build());
  }

  private List<PriceFinderOperaHotelAvailabilities> getHotelAvailabilities(PriceFinderLocationSearchCriteria criteria,
      LocalDate departureDate) {
    List<HotelDetails> hotelsForLocation = snowdropHotelLookupPort
        .getHotelsFromLocation(criteria.getLocationId(), properties.getLocationRadiusInMiles());
    Map<String, HotelDetails> hotelsMap = hotelsForLocation.stream().collect(
        Collectors.toMap(HotelDetails::getCode, hotelDetails -> hotelDetails));
    PriceFinderSearchCriteria priceFinderSearchCriteria =
        PriceFinderSearchCriteria.builder()
            .arrival(criteria.getArrival())
            .departure(departureDate.toString())
            .hotelCodes(hotelsForLocation.stream().map(HotelDetails::getCode).toList())
            .showMinimumNights(criteria.isShowMinimumNights())
            .build();

    log.debug("Fetching availabilities for PriceFinderSearchCriteria: {}", priceFinderSearchCriteria);
    List<PriceFinderResultSet> priceFinderResultSets = priceFinderAvailabilitiesPersistencePort.getLowestPricesByHotels(
        priceFinderSearchCriteria);

    List<PriceFinderOperaHotelAvailabilities> priceFinderHotelAvailabilities = postProcessorPort
        .processHotelResultSet(priceFinderResultSets, priceFinderSearchCriteria);

    priceFinderHotelAvailabilities.forEach(hotelAvailability ->
        Optional.ofNullable(hotelsMap.get(hotelAvailability.getHotelCode()))
            .ifPresent(hotelDetails -> {
              hotelAvailability.setHotelName(hotelDetails.getName());
              hotelAvailability.setDistanceFromSearchLocation(hotelDetails.getDistance());
            }));

    return priceFinderHotelAvailabilities;
  }

  private CalendarPriceFinderOperaHotelAvailabilities getHotelAvailabilitiesForCalendar(
      CalendarPriceFinderLocationSearchCriteria criteria) {
    int currentYear = LocalDate.now().getYear();
    int monthValue = Integer.parseInt(String.valueOf(criteria.getMonth()));
    LocalDate arrivalDate = LocalDate.of(currentYear, monthValue, 1);
    int lastDayOfTheMonth = arrivalDate.lengthOfMonth();
    LocalDate dateRangeEnd = arrivalDate.withDayOfMonth(lastDayOfTheMonth);

    /* receive hotels in a location in a milesRadius from snowdrop */
    List<HotelDetails> hotelsForLocation = snowdropHotelLookupPort
        .getHotelsFromLocation(criteria.getLocationId(), criteria.getMilesRadius());

    PriceFinderSearchCriteria priceFinderSearchCriteria =
        PriceFinderSearchCriteria.builder()
            .arrival(String.valueOf(arrivalDate))
            .departure(String.valueOf(dateRangeEnd))
            .hotelCodes(hotelsForLocation.stream().map(HotelDetails::getCode).toList())
            .showMinimumNights(true)
            .build();

    log.debug("Fetching availabilities for PriceFinderSearchCriteria: {}", priceFinderSearchCriteria);

    /* receive the hotel availabilities with lowest rates from DB */
    List<PriceFinderResultSet> priceFinderResultSets = priceFinderAvailabilitiesPersistencePort
        .getLowestPricesByHotels(priceFinderSearchCriteria);

    /* receive room substitution rules for DBroomType, 1A0C */
    List<RoomSubstitutionRuleResponse> roomSubstitutions = getRoomSubstitutions(
        List.of(DB_ROOM_TYPE), List.of(1), List.of(0), List.of(false), "PI"
    );

    Set<String> allowedRoomTypes = roomSubstitutions.stream()
        .flatMap(sub -> sub.getSubstitutionList().stream())
        .map(RoomSubstitution::getType)
        .collect(Collectors.toSet());

    /* apply room substitution rules on the data from the DB */
    List<PriceFinderResultSet> filteredPriceFinderResultSet = priceFinderResultSets.stream()
        .filter(resultSet -> resultSet.getRoomType() != null && allowedRoomTypes.contains(
            resultSet.getRoomType()))
        .toList();

    /* processing the data previously obtained and
     * transform it into a calendar with the lowestRate on each day
     */
    CalendarPriceFinderOperaHotelAvailabilities calendarPriceFinderOperaHotelAvailabilities = postProcessorPort
        .processHotelResultSetForCalendar(filteredPriceFinderResultSet, priceFinderSearchCriteria);

    calendarPriceFinderOperaHotelAvailabilities.setLocationId(criteria.getLocationId());
    calendarPriceFinderOperaHotelAvailabilities.setMilesRadius(criteria.getMilesRadius());
    calendarPriceFinderOperaHotelAvailabilities.setMonth(criteria.getMonth());

    return calendarPriceFinderOperaHotelAvailabilities;
  }

  private static boolean isSameMonth(LocalDate today, LocalDate arrivalDate) {
    return today.getYear() == arrivalDate.getYear() && today.getMonth() == arrivalDate.getMonth();
  }

  private List<RoomSubstitutionRuleResponse> getRoomSubstitutions(
      List<String> roomTypeList, List<Integer> adults, List<Integer> children,
      List<Boolean> cotsRequiredList, String channel) {

    List<RoomSubstitutionRuleResponse> roomSubstitutions = new ArrayList<>();

    var roomTypes = roomTypeList.iterator();
    var adultsNumber = adults.iterator();
    var childrenNumber = children.iterator();
    var cotsRequired = cotsRequiredList.iterator();

    while (roomTypes.hasNext() && adultsNumber.hasNext() && childrenNumber.hasNext()
        && cotsRequired.hasNext()) {
      var roomSubstitution = rulesAgentInPort.getRoomSubstitutionRule(
          RoomSubstitutionRuleRequest.builder()
              .roomType(roomTypes.next())
              .adults(adultsNumber.next())
              .children(childrenNumber.next())
              .channel(channel)
              .pms("OP")
              .build());
      roomSubstitution.getRequestDetails().setCotRequired(cotsRequired.next());
      roomSubstitutions.add(roomSubstitution);

    }
    return roomSubstitutions;
  }

}