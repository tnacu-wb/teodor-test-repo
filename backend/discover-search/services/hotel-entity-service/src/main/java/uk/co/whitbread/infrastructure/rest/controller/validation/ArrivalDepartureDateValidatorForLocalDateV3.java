package uk.co.whitbread.infrastructure.rest.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestV3Dto;


@Slf4j
public class ArrivalDepartureDateValidatorForLocalDateV3 implements
    ConstraintValidator<ArrivalDepartureDateConstraintForLocalDateV3,
        HotelAvailabilitiesByIdsRequestV3Dto>, ArrivalDepartureDateBaseValidator {

  @Override
  public void initialize(final ArrivalDepartureDateConstraintForLocalDateV3 constraintAnnotation) {
    //nothing to initialise
  }

  @Override
  public boolean isValid(HotelAvailabilitiesByIdsRequestV3Dto hotelAvailabilitiesByIdsRequestV3Dto,
      final ConstraintValidatorContext constraintValidatorContext) {

    if (hotelAvailabilitiesByIdsRequestV3Dto == null) {
      return false;
    }
    LocalDate arrivalDate = hotelAvailabilitiesByIdsRequestV3Dto.getArrivalDate();
    LocalDate departDate = hotelAvailabilitiesByIdsRequestV3Dto.getDepartureDate();
    log.trace("Executing \' ArrivalDepartureDateValidatorForLocalDate \' "
            + "for arrivalDate:{}, departureDate:{}", arrivalDate, departDate);


    return (!(isNullForLocalDate(arrivalDate, departDate)) && isValidArrivalAndDeparture(arrivalDate, departDate));


  }


}
