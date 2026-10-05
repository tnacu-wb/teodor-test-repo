package uk.co.whitbread.infrastructure.rest.controller.validation;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ArrivalDepartureDateBaseValidator<T> {

  Logger log = LoggerFactory.getLogger(ArrivalDepartureDateBaseValidator.class);

  default boolean isNullForLocalDate(final LocalDate arrivalDate, final LocalDate departureDate) {
    return (arrivalDate == null) || (departureDate == null);
  }

  default boolean isValidArrivalAndDeparture(final LocalDate arrivalDate, final LocalDate departureDate) {
    try {
      log.trace("Validating arrival date is before the departure date.... arrival - {} Departure - {}", arrivalDate,
          departureDate);
      LocalDate arrivalDateObject = LocalDate.parse(arrivalDate.format(ISO_LOCAL_DATE));
      LocalDate departureDateObject = LocalDate.parse(departureDate.format(ISO_LOCAL_DATE));

      if (arrivalDateObject.isBefore(LocalDate.now())
          || arrivalDateObject.equals(departureDateObject)
          || arrivalDateObject.isAfter(departureDateObject)) {
        log.error("Arrival date check failed, Arrival Date {} and Departure Date {}", arrivalDate, departureDate);
        return false;
      }
      return true;
    } catch (DateTimeParseException ex) {
      log.error("Date could not be parsed", ex);
      return false;
    }
  }
}

