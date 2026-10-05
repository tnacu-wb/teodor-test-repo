package uk.co.whitbread.reservation.domain.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsRequest;

public class HotelAvailabilityByIdsRequestDatesValidator implements
    ConstraintValidator<ValidAvailabilityDates, HotelAvailabilityByIdsRequest> {

  @Override
  public void initialize(ValidAvailabilityDates constraintAnnotation) {
    //Initialization not required.
  }

  @Override
  public boolean isValid(HotelAvailabilityByIdsRequest request, ConstraintValidatorContext context) {
    return ValidationUtils.validDates(request.getChannel(), LocalDate.parse(request.getArrivalDate()),
        LocalDate.parse(request.getDepartureDate()));
  }
}
