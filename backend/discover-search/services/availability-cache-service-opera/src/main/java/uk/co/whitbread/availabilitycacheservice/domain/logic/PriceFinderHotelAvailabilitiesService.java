package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.PaginationUtils.applyPagination;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.PriceFinderHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderLocationAvailabilititesOutPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.RulesAgentClientWebFlux;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SortingOption;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomTypePrecedence;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.CalendarPriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;

@Slf4j
@AllArgsConstructor
public class PriceFinderHotelAvailabilitiesService implements PriceFinderHotelAvailabilitiesPort {
  private final PriceFinderAvailabilitiesPersistencePort priceFinderAvailabilitiesPersistencePort;
  private final PriceFinderLocationAvailabilititesOutPort priceFinderLocationAvailabilitiesOutPort;
  private final PriceFinderAvailabilitiesPostProcessorPort postProcessorPort;
  private final RulesAgentClientWebFlux rulesAgentClientWebFlux;

  @Override
  public PriceFinderHotelAvailabilities getLowestPricesByLocationWithRoomTypeSubstitution(
      PriceFinderLocationSearchCriteria criteria, List<String> filterByRoomType) {

    LocalDate sortDate = StringUtils.isNotBlank(criteria.getSortDate()) ? LocalDate.parse(criteria.getSortDate()) :
        LocalDate.parse(criteria.getArrival());

    List<PriceFinderOperaHotelAvailabilities> allAvailabilities = getAvailabilitiesByLocation(criteria);

    Map<String, List<String>> substitutionsMap = getRoomSubstitutionsMap(filterByRoomType).block();
    if (substitutionsMap == null) {
      log.warn("Room substitutions map was unexpectedly null. Defaulting to an empty map.");
      substitutionsMap = Collections.emptyMap();
    }

    List<PriceFinderOperaHotelAvailabilities> filteredAvailabilities = getFilteredAvailabilities(criteria,
        allAvailabilities, substitutionsMap);

    List<PriceFinderOperaHotelAvailabilities> sorted = applySorting(filteredAvailabilities, criteria.getSortBy(),
        sortDate);
    List<PriceFinderOperaHotelAvailabilities> paginated = applyPagination(sorted, criteria.getInitialPageSize(),
        criteria.getLazyLoadPageSize(), criteria.getPage());

    return PriceFinderHotelAvailabilities.builder().lowestMonthlyRate(
            priceFinderLocationAvailabilitiesOutPort.getLowestMonthlyRateByLocation(criteria))
        .priceFinderOperaHotelAvailabilitiesDtoList(paginated).page(criteria.getPage())
        .pageSize(paginated.size()).total(filteredAvailabilities.size()).build();
  }

  private List<PriceFinderOperaHotelAvailabilities> getFilteredAvailabilities(
      PriceFinderLocationSearchCriteria criteria,
      List<PriceFinderOperaHotelAvailabilities> allAvailabilities,
      Map<String, List<String>> substitutionsMap) {

    Set<String> allowedRoomTypes = substitutionsMap.values().stream()
        .flatMap(List::stream)
        .collect(Collectors.toSet());

    List<PriceFinderOperaHotelAvailabilities> filteredAvailabilities = filterAvailabilitiesByRoomTypes(
        criteria, allAvailabilities, allowedRoomTypes);

    // Set roomCategory
    filteredAvailabilities.forEach(hotel -> hotel.getAvailabilities().forEach(availability -> {
      Set<String> availableRoomTypes = Arrays.stream(availability.getRoomType().split(","))
          .map(String::trim)
          .collect(Collectors.toSet());
      String roomCategory = getRoomCategory(substitutionsMap, availableRoomTypes);
      availability.setRoomCategory(roomCategory);
    }));
    return filteredAvailabilities;
  }

  private String getRoomCategory(Map<String, List<String>> substitutionsMap, Set<String> availableRoomTypes) {
    return Arrays.stream(RoomTypePrecedence.values())
        .map(Enum::name)
        .filter(substitutionsMap::containsKey)
        .filter(highLevelType ->
            !Collections.disjoint(substitutionsMap.get(highLevelType), availableRoomTypes))
        .findFirst()
        .orElse("");
  }

  private List<PriceFinderOperaHotelAvailabilities> filterAvailabilitiesByRoomTypes(
      PriceFinderLocationSearchCriteria criteria, List<PriceFinderOperaHotelAvailabilities> allAvailabilities,
      Set<String> allowedRoomTypes) {

    List<String> hotelCodes =
        allAvailabilities.stream().map(PriceFinderOperaHotelAvailabilities::getHotelCode)
            .distinct().toList();

    PriceFinderSearchCriteria pfSearchCriteria = convertToSearchCriteria(criteria, hotelCodes);

    List<PriceFinderResultSet> freshResultSets =
        priceFinderAvailabilitiesPersistencePort.getLowestPricesByHotels(pfSearchCriteria);

    List<PriceFinderResultSet> filteredResultSets = freshResultSets.stream().map(result -> {

      if (result.getRoomType() == null) {
        return null;
      }
      List<String> originalTypes = Arrays.asList(result.getRoomType().split(","));
      List<String> filteredTypes =
          originalTypes.stream().map(String::trim).filter(allowedRoomTypes::contains).toList();
      if (filteredTypes.isEmpty()) {
        return null;
      }
      result.setRoomType(String.join(",", filteredTypes));
      return result;
    }).filter(Objects::nonNull).toList();

    List<PriceFinderOperaHotelAvailabilities> filteredAvailabilities =
        postProcessorPort.processHotelResultSet(filteredResultSets, pfSearchCriteria, allowedRoomTypes);

    Map<String, PriceFinderOperaHotelAvailabilities> hotelDetailsMap =
        allAvailabilities.stream().collect(Collectors.toMap(
            PriceFinderOperaHotelAvailabilities::getHotelCode, a -> a, (a, b) -> a));
    filteredAvailabilities.forEach(hotel -> {
      PriceFinderOperaHotelAvailabilities details = hotelDetailsMap.get(hotel.getHotelCode());
      if (details != null) {
        hotel.setHotelName(details.getHotelName());
        hotel.setDistanceFromSearchLocation(details.getDistanceFromSearchLocation());
      }
    });
    return filteredAvailabilities;
  }

  private Mono<Map<String, List<String>>> getRoomSubstitutionsMap(List<String> filterByRoomType) {
    return Flux.fromIterable(filterByRoomType)
        .flatMap(roomTypeName -> {
          RoomTypePrecedence roomType = RoomTypePrecedence.valueOf(roomTypeName);
          List<Integer> occupancy = roomType.getOccupancy();
          final int adults = occupancy.get(0);
          final int children = occupancy.get(1);

          log.info("[PF-RULES-AGENT] Attempting rules-agent call for roomType: {} (adults={}, children={})", roomType,
              adults, children);

          return rulesAgentClientWebFlux.getRoomSubstitutions(roomTypeName, adults, children)
              .defaultIfEmpty(Collections.emptyMap())
              .map(response -> {
                List<String> substitutionTypes = Collections.emptyList();
                Object substitutionListObj = response.get("substitution-list");

                if (substitutionListObj instanceof List<?> rawList) {
                  log.info("[PF-RULES-AGENT] substitutionList for roomType {}: {}", roomType, substitutionListObj);
                  substitutionTypes = rawList.stream()
                      .filter(Map.class::isInstance)
                      .map(Map.class::cast)
                      .map(sub -> sub.get("type"))
                      .filter(String.class::isInstance)
                      .map(String.class::cast)
                      .toList();
                }

                log.info("[PF-RULES-AGENT] substitutionTypes for roomType {}: {}", roomType, substitutionTypes);
                return Map.entry(roomTypeName, substitutionTypes);
              })
              .doOnError(e -> log.error("[PF-RULES-AGENT] Exception for roomType {}: {}", roomType, e.toString(), e))
              .onErrorReturn(Map.entry(roomTypeName, Collections.emptyList()));
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        .doOnSuccess(substitutions -> log.info("[PF-RULES-AGENT] Final room substitutions map: {}", substitutions));
  }

  private PriceFinderSearchCriteria convertToSearchCriteria(PriceFinderLocationSearchCriteria criteria,
                                                            List<String> hotelCodes) {
    PriceFinderSearchCriteria pf = new PriceFinderSearchCriteria();
    pf.setArrival(criteria.getArrival());
    pf.setDeparture(LocalDate.parse(criteria.getArrival()).plusDays(criteria.getDaysRange() - 1L).toString());
    pf.setShowMinimumNights(criteria.isShowMinimumNights());
    pf.setHotelCodes(hotelCodes);
    return pf;
  }

  @Override
  public PriceFinderHotelAvailabilities getLowestPricesByLocation(PriceFinderLocationSearchCriteria criteria) {

    LocalDate arrivalDate = LocalDate.parse(criteria.getArrival());
    LocalDate sortDate = StringUtils.isNotBlank(criteria.getSortDate()) ? LocalDate.parse(criteria.getSortDate()) :
        arrivalDate;

    List<PriceFinderOperaHotelAvailabilities> lowestPricesByLocation = getAvailabilitiesByLocation(criteria);

    List<PriceFinderOperaHotelAvailabilities> sortedLowestPricesByLocation = applySorting(lowestPricesByLocation,
        criteria.getSortBy(), sortDate);
    List<PriceFinderOperaHotelAvailabilities> paginatedLowestPricesByLocation =
        applyPagination(sortedLowestPricesByLocation, criteria.getInitialPageSize(), criteria.getLazyLoadPageSize(),
            criteria.getPage());

    return PriceFinderHotelAvailabilities.builder().lowestMonthlyRate(
            priceFinderLocationAvailabilitiesOutPort.getLowestMonthlyRateByLocation(criteria))
        .priceFinderOperaHotelAvailabilitiesDtoList(paginatedLowestPricesByLocation)
        .page(criteria.getPage()).pageSize(paginatedLowestPricesByLocation.size())
        .total(lowestPricesByLocation.size()).build();
  }

  private List<PriceFinderOperaHotelAvailabilities> getAvailabilitiesByLocation(
      PriceFinderLocationSearchCriteria criteria) {
    LocalDate arrivalDate = LocalDate.parse(criteria.getArrival());
    LocalDate dateRangeEnd = arrivalDate.plusDays(criteria.getDaysRange() - 1L);

    return priceFinderLocationAvailabilitiesOutPort.getAvailabilitiesByLocation(criteria, dateRangeEnd);
  }

  @Override
  public CalendarPriceFinderHotelAvailabilities getLowestPricesByLocationForCalendar(
      CalendarPriceFinderLocationSearchCriteria calendarCriteria) {

    CalendarPriceFinderOperaHotelAvailabilities calendarPriceFinderOperaHotelAvailabilities =
        priceFinderLocationAvailabilitiesOutPort.getAvailabilitiesByLocationForCalendar(calendarCriteria);

    return CalendarPriceFinderHotelAvailabilities.builder()
        .calendarPriceFinderOperaHotelAvailabilitiesDtoList(calendarPriceFinderOperaHotelAvailabilities)
        .build();
  }

  @Override
  public List<PriceFinderOperaHotelAvailabilities> getLowestPricesByHotel(
      PriceFinderSearchCriteria priceFinderSearchCriteria) {

    configureDateRangeForSearch(priceFinderSearchCriteria);

    List<PriceFinderResultSet> priceFinderResultSets =
        priceFinderAvailabilitiesPersistencePort.getLowestPricesByHotels(priceFinderSearchCriteria);
    return postProcessorPort.processHotelResultSet(priceFinderResultSets, priceFinderSearchCriteria);
  }

  private static void configureDateRangeForSearch(PriceFinderSearchCriteria priceFinderSearchCriteria) {
    LocalDate arrivalDate = LocalDate.parse(priceFinderSearchCriteria.getArrival());
    LocalDate currentDate = LocalDate.now();

    if (currentDate.isEqual(arrivalDate)) {
      priceFinderSearchCriteria.setDeparture(String.valueOf(currentDate.plusDays(14)));
    } else if (arrivalDate.isBefore(currentDate.plusDays(14))) {
      priceFinderSearchCriteria.setArrival(String.valueOf(currentDate));
      priceFinderSearchCriteria.setDeparture(String.valueOf(arrivalDate.plusDays(14)));
    } else {
      priceFinderSearchCriteria.setArrival(String.valueOf(arrivalDate.minusDays(14)));
      priceFinderSearchCriteria.setDeparture(String.valueOf(arrivalDate.plusDays(14)));
    }
  }

  private static List<PriceFinderOperaHotelAvailabilities> applySorting(
      List<PriceFinderOperaHotelAvailabilities> lowestPricesByLocation, SortingOption sortBy, LocalDate sortDate) {
    if (SortingOption.PRICE.equals(sortBy)) {
      return lowestPricesByLocation.stream()
          .sorted(Comparator.comparing(hotelAvailabilities ->
                  getMinimumRateForDate(hotelAvailabilities, sortDate),
              Comparator.nullsLast(Comparator.naturalOrder()))).toList();
    } else {
      return lowestPricesByLocation.stream()
          .sorted(Comparator.comparing(PriceFinderOperaHotelAvailabilities::getDistanceFromSearchLocation,
              Comparator.nullsLast(Comparator.naturalOrder()))).toList();
    }
  }

  private static BigDecimal getMinimumRateForDate(PriceFinderOperaHotelAvailabilities hotel, LocalDate sortDate) {
    return hotel.getAvailabilities().stream()
        .filter(a -> sortDate.equals(LocalDate.parse(a.getAvailableDate())))
        .findFirst()
        .filter(availabilities -> availabilities.getMinimumRate()
            .compareTo(BigDecimal.ZERO) > 0).map(Availabilities::getMinimumRate).orElse(null);
  }

}
