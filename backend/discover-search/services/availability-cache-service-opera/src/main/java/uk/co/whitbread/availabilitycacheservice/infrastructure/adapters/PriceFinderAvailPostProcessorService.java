package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static java.math.BigDecimal.ZERO;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldApplyCityTax;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldCalculateCityTax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.cglib.core.Local;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.LowestRate;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.RateCodeMinimumRate;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.AvailabilityProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;

@Slf4j
@AllArgsConstructor
public class PriceFinderAvailPostProcessorService implements PriceFinderAvailabilitiesPostProcessorPort {

  private static final String EMPLOYEE_RATE_PLAN_CODE = "EMP01";
  public static final int CLOSED_TO_BOOK = 666;
  public static final int CLOSED_TO_ARRIVAL = 999;

  private final LosRestrictionPort losRestrictionPort;
  private final AvailabilityProperties properties;
  private final ContentClientLookUpService contentClientLookUpService;
  private final CityTaxFeatureUtil cityTaxFeatureUtil;

  @Override
  public List<PriceFinderOperaHotelAvailabilities> processHotelResultSet(
          List<PriceFinderResultSet> priceFinderResultSets, PriceFinderSearchCriteria priceFinderSearchCriteria) {
    return processHotelResultSet(priceFinderResultSets, priceFinderSearchCriteria, null);
  }

  // Overloaded method to accept allowedRoomTypes
  public List<PriceFinderOperaHotelAvailabilities> processHotelResultSet(
          List<PriceFinderResultSet> priceFinderResultSets,
          PriceFinderSearchCriteria priceFinderSearchCriteria, Set<String> allowedRoomTypes) {
    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilitiesList = new ArrayList<>();
    final Map<String, List<PriceFinderResultSet>> resultSetsByHotel =
            priceFinderResultSets.stream().collect(Collectors.groupingBy(PriceFinderResultSet::getHotelCode));

    HotelsCityTaxInfo hotelsCityTaxInfo = getHotelsCityTaxInfoIfNeeded(priceFinderSearchCriteria);

    for (String hotelCode : priceFinderSearchCriteria.getHotelCodes()) {
      PriceFinderOperaHotelAvailabilities priceFinderOperaHotelAvailabilities =
              new PriceFinderOperaHotelAvailabilities();
      priceFinderOperaHotelAvailabilities.setHotelCode(hotelCode);

      final Set<Availabilities> availabilities =
              getAvailabilitiesFromResultSetForGivenHotel(priceFinderSearchCriteria, resultSetsByHotel.get(hotelCode),
                      hotelsCityTaxInfo, allowedRoomTypes);
      priceFinderOperaHotelAvailabilities.setAvailabilities(availabilities);
      priceFinderOperaHotelAvailabilitiesList.add(priceFinderOperaHotelAvailabilities);
    }
    return priceFinderOperaHotelAvailabilitiesList;
  }

  @Override
  public CalendarPriceFinderOperaHotelAvailabilities processHotelResultSetForCalendar(
      List<PriceFinderResultSet> filteredPriceFinderResultSet,
      PriceFinderSearchCriteria priceFinderSearchCriteria) {

    /* processHotelResultSet() takes the raw availability data
     * and sorts it by the minimum rate per day and applies business rules on it
     * structures the data per hotel
     */
    List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities = processHotelResultSet(
        filteredPriceFinderResultSet, priceFinderSearchCriteria);

    Set<PriceFinderOperaHotelAvailabilities> filteredHotelsWithAvailabilities =
        priceFinderOperaHotelAvailabilities.stream()
        .filter(hotelAvailabilities -> !hotelAvailabilities.getAvailabilities().isEmpty())
        .collect(Collectors.toSet());

    /* extract the currency from the first element from the list */
    Optional<Availabilities> firstAvailabilityWithCurrency = filteredHotelsWithAvailabilities.stream()
        .flatMap(hotelAvailabilities -> hotelAvailabilities.getAvailabilities().stream())
        .filter(availability -> availability.getCurrency() != null && !availability.getCurrency()
            .isEmpty()).findFirst();

    String firstExtractedCurrencyCode =
        firstAvailabilityWithCurrency.isPresent() ? firstAvailabilityWithCurrency.get()
            .getCurrency() : null;

    Map<LocalDate, LowestRate> lowestRatesMap = filteredHotelsWithAvailabilities.stream()
        .flatMap(hotelAvailabilities -> hotelAvailabilities.getAvailabilities().stream()
            .map(availability -> new LowestRate(
                LocalDate.parse(availability.getAvailableDate(), DateTimeFormatter.ISO_LOCAL_DATE),
                availability.getMinimumRate()))
        )/* take the lowest available rate per day across all hotels */
        .collect(
            Collectors.groupingBy(
                LowestRate::getAvailableDate,
                Collectors.minBy(
                    PriceFinderAvailPostProcessorService::compareLowestRatesExcludingZero)
            ))
        .entrySet().stream()
        .filter(entry -> entry.getValue().isPresent())
        .collect(
            Collectors.toMap(
                Entry::getKey,
                entry -> entry.getValue().get()
            )
        );

    CalendarPriceFinderOperaHotelAvailabilities calendar =
        new CalendarPriceFinderOperaHotelAvailabilities();
    calendar.setCurrency(firstExtractedCurrencyCode);

    calendar.setLowestRates(
        new TreeSet<>(lowestRatesMap.values())
    );

    return calendar;
  }

  private static int compareLowestRatesExcludingZero(LowestRate rate1, LowestRate rate2) {
    /* handle corner case when the minimum rate is 0
     * and there is another hotel with availability on that day
    */
    BigDecimal amount1 = rate1.getMinimumRate();
    BigDecimal amount2 = rate2.getMinimumRate();

    boolean isRate1Zero = amount1.compareTo(ZERO) == 0;
    boolean isRate2Zero = amount2.compareTo(ZERO) == 0;

    // If one is zero and the other isn't, prefer the non-zero rate
    if (isRate1Zero && !isRate2Zero) {
      return 1;
    }
    if (!isRate1Zero && isRate2Zero) {
      return -1;
    }

    return amount1.compareTo(amount2);
  }


  private @Nullable HotelsCityTaxInfo getHotelsCityTaxInfoIfNeeded(
          PriceFinderSearchCriteria priceFinderSearchCriteria) {
    HotelsCityTaxInfo hotelsCityTaxInfo = null;
    if (cityTaxFeatureUtil.isFeatureEnabled()) {
      log.debug("City Tax feature is enabled. Fetching city tax info for hotels");
      hotelsCityTaxInfo =
              contentClientLookUpService.getHotelsCityTaxInfo(Optional.ofNullable(
                              priceFinderSearchCriteria.getCountry())
                      .orElse("gb"), Optional.ofNullable(priceFinderSearchCriteria.getLanguage())
                      .orElse("en"), priceFinderSearchCriteria.getHotelCodes());
    }
    return hotelsCityTaxInfo;
  }

  private Set<Availabilities> getAvailabilitiesFromResultSetForGivenHotel(
          final PriceFinderSearchCriteria priceFinderSearchCriteria,
          final List<PriceFinderResultSet> priceFinderResultSets,
          HotelsCityTaxInfo hotelsCityTaxInfo, Set<String> allowedRoomTypes) {

    if (CollectionUtils.isEmpty(priceFinderResultSets)) {
      return Collections.emptySet();
    }

    final Set<LocalDate> availableDatesForHotel =
            priceFinderResultSets.stream().map(PriceFinderResultSet::getAvailableDate).collect(Collectors.toSet());
    log.trace("availableDatesForHotel - {}", availableDatesForHotel);

    Set<Availabilities> availabilitiesSet =
            new TreeSet<>(Comparator.comparing(o -> LocalDate.parse(o.getAvailableDate(),
                    DateTimeFormatter.ISO_LOCAL_DATE)));

    Map<LocalDate, List<PriceFinderResultSet>> hotelResultSetMapForRates =
            priceFinderResultSets.stream().collect(Collectors.groupingBy(PriceFinderResultSet::getAvailableDate));

    // Process each PriceFinderResultSet for the given arrival date
    LocalDate endDate = LocalDate.parse(priceFinderSearchCriteria.getDeparture()).plusDays(1);
    Stream.iterate(LocalDate.parse(priceFinderSearchCriteria.getArrival()),
            d -> d.isBefore(endDate),
            d -> d.plusDays(1)).sorted().forEach(availableDate -> {
              List<PriceFinderResultSet> ratesForAvailableDate = hotelResultSetMapForRates.get(availableDate);

              if (CollectionUtils.isEmpty(ratesForAvailableDate)) {
                availabilitiesSet.add(getEmptyAvailability(availableDate));
                return;
              }

              // Filter by allowedRoomTypes (if provided)
              if (allowedRoomTypes != null && !allowedRoomTypes.isEmpty()) {
                log.info("[PF-ROOM-FILTER] roomTypes in ratesForAvailableDate after filtering: {}",
                    ratesForAvailableDate.stream().map(PriceFinderResultSet::getRoomType).distinct().toList());
                ratesForAvailableDate.removeIf(r -> !allowedRoomTypes.contains(r.getRoomType()));
              }

              ratesForAvailableDate.removeIf(this::isClosedRestrictionApplicable);

              if (isRatesForAvailableDateEmpty(availableDate, ratesForAvailableDate, availabilitiesSet)) {
                return;
              }

              filterAvailableRatesByClassification(ratesForAvailableDate);

              boolean hasLosRestriction = isLosRestriction(
                  priceFinderSearchCriteria.isShowMinimumNights(), ratesForAvailableDate,
                  availableDate);
              List<PriceFinderResultSet> minimumRates;
              int minimumNights = 0;
              BigDecimal minAmount;

              if (priceFinderSearchCriteria.isShowMinimumNights()) {
                minimumNights = filterMinimumNightsByRestrictions(ratesForAvailableDate);
                ratesForAvailableDate = filterRatesByMinimumNights(ratesForAvailableDate, minimumNights);
              }

              if (priceFinderSearchCriteria.getRooms() > 1) { // logic for multi room Price finder
                List<PriceFinderResultSet> sortedByMinimumRate =
                        ratesForAvailableDate.stream().sorted(Comparator.comparing(
                                PriceFinderResultSet::getMinimumRate)).toList();
                LinkedHashMap<RateCodeMinimumRate, Integer> minimumRateMap = new LinkedHashMap<>();
                int rooms = priceFinderSearchCriteria.getRooms();

                RateCodeMinimumRate foundRateCodeAndMinimumRate = getRateCodeMinimumRate(sortedByMinimumRate,
                        minimumRateMap, rooms);

                if (foundRateCodeAndMinimumRate == null) {
                  availabilitiesSet.add(getEmptyAvailability(availableDate));
                  return;
                }

                final String rateCode = foundRateCodeAndMinimumRate.rateCode();
                minAmount = foundRateCodeAndMinimumRate.minimumRate();
                final BigDecimal finalMinAmount = minAmount;

                minimumRates =
                        sortedByMinimumRate.stream().filter(resultSet -> resultSet.getMinimumRate()
                                .equals(finalMinAmount) && resultSet.getRateCode().equals(rateCode)).toList();
              } else { // logic for single room Price finder
                minAmount =
                        ratesForAvailableDate.stream().map(PriceFinderResultSet::getMinimumRate)
                                .min(Comparator.naturalOrder()).orElseGet(() -> BigDecimal.valueOf(0.00));
                final BigDecimal finalMinAmount = minAmount;

                minimumRates =
                        ratesForAvailableDate.stream().filter(resultSet -> resultSet
                                .getMinimumRate().equals(finalMinAmount)).toList();
              }

              Optional<BigDecimal> amountWithCityTax = getAmountWithCityTax(hotelsCityTaxInfo, minimumRates, minAmount,
                      availableDate);

              Availabilities availabilities =
                  getAvailability(availableDate, minimumRates, amountWithCityTax.orElse(minAmount),
                      hasLosRestriction, minimumNights, priceFinderSearchCriteria.getRooms());

              availabilitiesSet.add(availabilities);
            });

    return availabilitiesSet;
  }

  private static RateCodeMinimumRate getRateCodeMinimumRate(
          List<PriceFinderResultSet> sortedByMinimumRate,
          LinkedHashMap<RateCodeMinimumRate, Integer> minimumRateMap, int rooms) {
    RateCodeMinimumRate foundRateCodeAndMinimumRate = null;
    BigDecimal lastMinimumRate = ZERO;

    for (PriceFinderResultSet resultSet : sortedByMinimumRate) {
      if (resultSet.getMinimumRate().compareTo(lastMinimumRate) > 0) {
        /*
         * cleared map when the minimum rate changes because
         * previous values are not relevant anymore (not enough rooms at this rate)
         * optimization for searching in the hashmap
         */
        minimumRateMap.clear();
        lastMinimumRate = resultSet.getMinimumRate(); // last min rate found
      }

      RateCodeMinimumRate key = new RateCodeMinimumRate(resultSet.getRateCode(), resultSet.getMinimumRate());
      Integer quantity = minimumRateMap.get(key);

      if (quantity != null) { /* rate code - min rate pair already exists in the map */
        quantity = quantity + resultSet.getQuantity();
        if (quantity >= rooms) {
          foundRateCodeAndMinimumRate = key;
          /* found the first rateCode - minRate pair that
           * satisfies the room requirement
           */
          break;
        }
        minimumRateMap.put(key, quantity);
      } else { /* rate code - min rate pair does not exist in the map */
        minimumRateMap.put(new RateCodeMinimumRate(resultSet.getRateCode(), resultSet.getMinimumRate()),
                resultSet.getQuantity());
      }
    }
    return foundRateCodeAndMinimumRate;
  }

  private Optional<BigDecimal> getAmountWithCityTax(HotelsCityTaxInfo hotelsCityTaxInfo,
                                                    List<PriceFinderResultSet> minimumRates, BigDecimal amount,
                                                    LocalDate availableDate) {
    if (minimumRates.isEmpty()) {
      return Optional.empty();
    }

    String hotelCode = minimumRates.get(0).getHotelCode();
    BigDecimal ocdAmountWithCityTax = minimumRates.get(0).getMinimumRateWithCityTax();
    if (shouldApplyCityTax(cityTaxFeatureUtil.isFeatureEnabled(), hotelCode, hotelsCityTaxInfo, availableDate)) {
      return Optional.of(shouldCalculateCityTax(cityTaxFeatureUtil.isFallbackEnabled(), ocdAmountWithCityTax)
              ? CityTaxUtil.getAmountWithCityTax(hotelsCityTaxInfo.getHotelsCityTaxes().get(hotelCode), hotelCode,
              amount, 1, 1) : ocdAmountWithCityTax);
    }

    return Optional.empty();
  }

  private static boolean isRatesForAvailableDateEmpty(LocalDate availableDate,
                                                      List<PriceFinderResultSet> ratesForAvailableDate,
                                                      Set<Availabilities> availabilitiesSet) {

    if (CollectionUtils.isEmpty(ratesForAvailableDate)) {
      Availabilities emptyAvailability = getEmptyAvailability(availableDate);
      emptyAvailability.setHasClosedRestriction(true);
      availabilitiesSet.add(emptyAvailability);
      return true;
    }
    return false;
  }

  private static Availabilities getAvailability(LocalDate availableDate,
      List<PriceFinderResultSet> minimumRates,
      BigDecimal amount, boolean hasLosRestriction, int minimumNights, int roomRequested) {

    String currency =
        minimumRates.stream()
            .map(PriceFinderResultSet::getCurrency).distinct().collect(Collectors.joining(","));
    String rateCode =
        minimumRates.stream()
            .map(PriceFinderResultSet::getRateCode).distinct().collect(Collectors.joining(","));
    String rateClassification =
        minimumRates.stream()
            .map(PriceFinderResultSet::getRateClassification).distinct().collect(Collectors.joining(
                ","));
    String roomType =
        minimumRates.stream()
            .map(PriceFinderResultSet::getRoomType).distinct().collect(Collectors.joining(","));
    int quantity = minimumRates.stream().mapToInt(PriceFinderResultSet::getQuantity).sum();

    return Availabilities.builder()
        .availableDate(availableDate.toString())
        .minimumRate(amount)
        .finalPrice(amount.multiply(BigDecimal.valueOf(roomRequested)))
        .currency(currency.equals("E") ? "EUR" : "GBP")
        .rateCode(rateCode)
        .rateClassification(rateClassification)
        .roomType(roomType)
        .quantity(quantity).hasClosedRestriction(false).hasMlosRestriction(hasLosRestriction)
        .minimumNights(minimumNights == 0 ? 1 : minimumNights).build();
  }

  private void filterAvailableRatesByClassification(List<PriceFinderResultSet> ratesForAvailableDate) {
    // get list of ratePlanCodes for the available date
    List<String> ratePlanCodes =
            ratesForAvailableDate.stream().map(PriceFinderResultSet::getRateCode).distinct().toList();

    // filter out rates
    boolean empRatePlanCodeAvailable =
            ratePlanCodes.stream().anyMatch(ratePlanCode -> StringUtils.equalsIgnoreCase(EMPLOYEE_RATE_PLAN_CODE,
                    ratePlanCode));
    Predicate<PriceFinderResultSet> filterClasses;
    if (empRatePlanCodeAvailable) {
      filterClasses =
              hotelRates ->
                      !properties.getEmployeeRatePlanClasses().contains(hotelRates.getRateClassification());
    } else {
      filterClasses = hotelRates ->
              !properties.getRatePlanClasses().contains(hotelRates.getRateClassification());
    }
    ratesForAvailableDate.removeIf(filterClasses);
  }

  private static Availabilities getEmptyAvailability(LocalDate availableDate) {
    return Availabilities.builder()
        .availableDate(availableDate.toString())
        .minimumRate(BigDecimal.valueOf(0.00))
        .finalPrice(BigDecimal.valueOf(0.00))
        .minimumNights(0)
        .currency(StringUtils.EMPTY)
        .rateCode(StringUtils.EMPTY)
        .rateClassification(StringUtils.EMPTY)
        .roomType(StringUtils.EMPTY)
        .build();
  }

  private boolean isLosApplicable(final PriceFinderResultSet priceFinderResultSet, final LocalDate arrivalDate) {

    LocalDate departureDate = arrivalDate.plusDays(1);

    SearchCriteria criteria =
            SearchCriteria.builder().hotelCodes(Collections.singletonList(
                            priceFinderResultSet.getHotelCode()))
                    .arrival(arrivalDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .departure(departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE)).build();

    RatePlan ratePlan =
            RatePlan.builder().code(priceFinderResultSet.getRateCode())
                    .minNights(priceFinderResultSet.getMinNights())
                    .maxNights(priceFinderResultSet.getMaxNights()).build();

    return losRestrictionPort.isLosApplicable(ratePlan, criteria);
  }

  private boolean isClosedRestrictionApplicable(PriceFinderResultSet priceFinderResultSet) {
    return null != priceFinderResultSet.getRateCode() && (priceFinderResultSet.getMinNights() == CLOSED_TO_BOOK
                    || priceFinderResultSet.getMinNights() == CLOSED_TO_ARRIVAL);
  }

  private boolean isLosRestriction(boolean multiRoomPriceFinderSearchCriteria,
                                   List<PriceFinderResultSet> ratesForAvailableDate, LocalDate availableDate) {

    boolean hasLosRestriction = false;

    if (!multiRoomPriceFinderSearchCriteria) {
      /* LOS restrictions
       * price finder returns only availability for a night.
       * So if minNights is set to anything more than 1,
       * then no availability is returned back for that date
       */
      hasLosRestriction = ratesForAvailableDate.removeIf(priceFinderResultSet ->
              isLosApplicable(priceFinderResultSet, availableDate));
    }
    return hasLosRestriction;
  }

  private static int filterMinimumNightsByRestrictions(List<PriceFinderResultSet> ratesForAvailableDate) {
    return ratesForAvailableDate.stream().map(PriceFinderResultSet::getMinNights).filter(minNights ->
                    minNights != CLOSED_TO_BOOK && minNights != CLOSED_TO_ARRIVAL)
            .min(Comparator.naturalOrder()).orElse(0);
  }

  private static List<PriceFinderResultSet> filterRatesByMinimumNights(List<PriceFinderResultSet> ratesForAvailableDate,
                                                                       final int minNights) {
    return ratesForAvailableDate.stream().filter(rate -> rate.getMinNights() == minNights).toList();
  }
}
