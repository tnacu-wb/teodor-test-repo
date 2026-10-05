package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.gqt;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.GqtSearchCriteria;

@Slf4j
public class GqtArrivalDepartureDateValidator implements
    ConstraintValidator<GqtArrivalDepartureDateConstraint, GqtSearchCriteria> {

  @Override
  public void initialize(final GqtArrivalDepartureDateConstraint constraintAnnotation) {
    //initialization method for the constraint
  }

  @Override
  public boolean isValid(final GqtSearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing \' ArrivalDepartureDateValidator \' for :{}", searchCriteria);
    return (!(isNull(searchCriteria)) && isValidArrivalAndDeparture(searchCriteria));
  }

  public boolean isNull(final GqtSearchCriteria searchCriteria) {

    return (searchCriteria == null || searchCriteria.getArrival() == null
        || searchCriteria.getDeparture() == null);
  }

  public boolean isValidArrivalAndDeparture(final GqtSearchCriteria searchCriteria) {
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
