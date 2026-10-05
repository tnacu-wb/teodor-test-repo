package uk.co.whitbread.reservation.domain.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityRequest;

public class HotelAvailabilityRequestDatesValidator implements
    ConstraintValidator<ValidAvailabilityDates, HotelAvailabilityRequest> {

  @Override
  public void initialize(ValidAvailabilityDates constraintAnnotation) {
    //Initialization not required.
  }

  @Override
  public boolean isValid(HotelAvailabilityRequest request, ConstraintValidatorContext context) {
    return ValidationUtils.validDates(request.getChannel(), LocalDate.parse(request.getArrivalDate()),
        LocalDate.parse(request.getDepartureDate()));
  }
}
