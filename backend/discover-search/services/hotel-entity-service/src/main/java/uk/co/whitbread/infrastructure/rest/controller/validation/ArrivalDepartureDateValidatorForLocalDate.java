package uk.co.whitbread.infrastructure.rest.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestV2Dto;


@Slf4j
public class ArrivalDepartureDateValidatorForLocalDate implements
    ConstraintValidator<ArrivalDepartureDateConstraintForLocalDate,
        HotelAvailabilitiesByIdsRequestV2Dto>, ArrivalDepartureDateBaseValidator {

  @Override
  public void initialize(final ArrivalDepartureDateConstraintForLocalDate constraintAnnotation) {
    //nothing to initialise
  }

  @Override
  public boolean isValid(HotelAvailabilitiesByIdsRequestV2Dto hotelAvailabilitiesByIdsRequestV2Dto,
      final ConstraintValidatorContext constraintValidatorContext) {

    if (hotelAvailabilitiesByIdsRequestV2Dto == null) {
      return false;
    }
    LocalDate arrivalDate = hotelAvailabilitiesByIdsRequestV2Dto.getArrivalDate();
    LocalDate departDate = hotelAvailabilitiesByIdsRequestV2Dto.getDepartureDate();
    log.trace("Executing \' ArrivalDepartureDateValidatorForLocalDate \' "
        + "for arrivalDate:{}, departureDate:{}", arrivalDate, departDate);


    return (!(isNullForLocalDate(arrivalDate, departDate)) && isValidArrivalAndDeparture(arrivalDate, departDate));


  }


}
