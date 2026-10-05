package uk.co.whitbread.reservation.domain.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsV2Request;

public class HotelAvailabilityByIdsV2RequestDatesValidator implements
    ConstraintValidator<ValidAvailabilityDates, HotelAvailabilityByIdsV2Request> {

  @Override
  public void initialize(ValidAvailabilityDates constraintAnnotation) {
    //Initialization not required.
  }

  @Override
  public boolean isValid(HotelAvailabilityByIdsV2Request request, ConstraintValidatorContext context) {
    return ValidationUtils.validDates(request.getBookingChannel().getChannel(), request.getArrivalDate(),
        request.getDepartureDate());
  }
}
