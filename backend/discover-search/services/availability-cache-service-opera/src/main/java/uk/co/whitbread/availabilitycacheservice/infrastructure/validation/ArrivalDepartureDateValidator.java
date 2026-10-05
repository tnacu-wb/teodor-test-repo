package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
public class ArrivalDepartureDateValidator implements
    ConstraintValidator<ArrivalDepartureDateConstraint, SearchCriteria> {

  @Override
  public void initialize(final ArrivalDepartureDateConstraint constraintAnnotation) {
    //Initilization of input "ArrivalDepartureDateConstraint" object not required in this scenario
  }

  @Override
  public boolean isValid(final SearchCriteria searchCriteria, ConstraintValidatorContext constraintValidatorContext) {
    log.trace("Executing \' ArrivalDepartureDateValidator \' for :{}", searchCriteria);
    return (!(isNull(searchCriteria)) && isValidArrivalAndDeparture(searchCriteria));
  }

  public boolean isNull(final SearchCriteria searchCriteria) {

    return (searchCriteria == null || searchCriteria.getArrival() == null
        || searchCriteria.getDeparture() == null);
  }

  public boolean isValidArrivalAndDeparture(final SearchCriteria searchCriteria) {
    try {
      log.trace("Validating arrival date is before the departure date.... arrival - {} Departure - {}",
          searchCriteria.getArrival(), searchCriteria.getDeparture());

      final LocalDate arrivalDate = LocalDate.parse(searchCriteria.getArrival(), DateTimeFormatter.ISO_LOCAL_DATE);
      final LocalDate departureDate = LocalDate.parse(searchCriteria.getDeparture(), DateTimeFormatter.ISO_LOCAL_DATE);

      if (arrivalDate.isBefore(LocalDate.now())
          || arrivalDate.equals(departureDate)
          || arrivalDate.isAfter(departureDate)) {
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
