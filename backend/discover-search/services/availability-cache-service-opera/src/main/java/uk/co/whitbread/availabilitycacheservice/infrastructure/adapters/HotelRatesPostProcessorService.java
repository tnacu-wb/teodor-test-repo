package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelRatesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
public class HotelRatesPostProcessorService implements HotelRatesPostProcessorPort {

  public boolean isAllDaysRatesReturned(final SearchCriteria searchCriteria,
      List<HotelAvailabilitiesResultSet> hotelResultSetWithRates,
      final String rateClassification,
      final String hotelCode) {
    if (hotelResultSetWithRates.isEmpty()) {
      log.debug("hotelResultSetWithRates is empty for the rateClassification - {} and hotel - {} ,so return false",
          rateClassification, hotelCode);
      return false;
    }
    final LocalDate arrivalDate = LocalDate.parse(searchCriteria.getArrival());
    final LocalDate departureDate = LocalDate.parse(searchCriteria.getDeparture());
    final long daysBetween = DAYS.between(arrivalDate, departureDate);
    final long distinctRoomTypesCounter = Arrays.stream(searchCriteria.getType()).distinct().count();
    log.debug("distinctRoomTypesCounter {} - and daysBetween {}",
        distinctRoomTypesCounter, daysBetween);
    final long numberOfExpectedRatesForDays = daysBetween * distinctRoomTypesCounter;
    final Map<String, Long> ratesWithClassificationCounter = hotelResultSetWithRates.stream()
        .collect(
            Collectors.groupingBy(HotelAvailabilitiesResultSet::getRateClassification,
                Collectors.counting()));
    Long ratesFetchedCounterFromDb = ratesWithClassificationCounter.get(rateClassification);
    log.debug(
        "ratesFetchedCounterFromDb - {}  ,daysBetween - {} ,numberOfExpectedRatesForDays - {} "
            + " for rateClassification - {} and hotel - {}",
        ratesFetchedCounterFromDb, daysBetween, numberOfExpectedRatesForDays, rateClassification,
        hotelCode);
    if (ratesFetchedCounterFromDb != numberOfExpectedRatesForDays) {
      log.debug(
          "ratesFetchedCounterFromDb - {} & numberOfExpectedRatesForDays - {} are not equal for the days - {} "
              + ",so return false for rateClassification -{} and hotel - {}",
          ratesFetchedCounterFromDb, numberOfExpectedRatesForDays, daysBetween,
          rateClassification, hotelCode);
      return false;
    }
    return true;
  }
}
