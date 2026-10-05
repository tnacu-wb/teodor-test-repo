package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaSearchCriteria;

@Slf4j
public class OperaArrivalDepartureDateValidator implements
    ConstraintValidator<OperaArrivalDepartureDateConstraint, OperaSearchCriteria> {

  @Override
  public void initialize(final OperaArrivalDepartureDateConstraint constraintAnnotation) {
    //initialization method for the constraint
  }

  @Override
  public boolean isValid(final OperaSearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing \' ArrivalDepartureDateValidator \' for :{}", searchCriteria);
    return (!(isNull(searchCriteria)) && isValidArrivalAndDeparture(searchCriteria));
  }

  public boolean isNull(final OperaSearchCriteria searchCriteria) {

    return (searchCriteria == null || searchCriteria.getArrival() == null
        || searchCriteria.getDeparture() == null);
  }

  public boolean isValidArrivalAndDeparture(final OperaSearchCriteria searchCriteria) {
    try {
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
