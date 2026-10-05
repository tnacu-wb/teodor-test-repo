package uk.co.whitbread.infrastructure.rest.controller.validation;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilityRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelInventoryRequestDto;

@Slf4j
public class ArrivalDepartureDateValidator implements
    ConstraintValidator<ArrivalDepartureDateConstraint,
        Object> {

  private String arrivalDate;
  private String departureDate;

  @Override
  public void initialize(final ArrivalDepartureDateConstraint constraintAnnotation) {
    //nothing to initialise
  }

  @Override
  public boolean isValid(final Object obj,
      final ConstraintValidatorContext constraintValidatorContext) {
    log.trace("Executing \' ArrivalDepartureDateValidator \' "
            + "for arrivalDate:{}, departureDate:{}",
        arrivalDate, departureDate);
    if (null == obj) {
      return false;
    }
    setArrivalDepartureDates(obj);
    return (!(isNull(arrivalDate, departureDate))
        && isValidArrivalAndDeparture(arrivalDate, departureDate));
  }

  private void setArrivalDepartureDates(final Object obj) {
    if (obj instanceof HotelInventoryRequestDto hotelInventoryReq) {
      arrivalDate = hotelInventoryReq.getDateRangeStart();
      departureDate = hotelInventoryReq.getDateRangeEnd();
    } else if (obj instanceof HotelAvailabilitiesByIdsRequestDto hotelAvailabilitiesByIdsReq) {
      arrivalDate = hotelAvailabilitiesByIdsReq.getArrivalDate();
      departureDate = hotelAvailabilitiesByIdsReq.getDepartureDate();
    } else if (obj instanceof HotelAvailabilityRequestDto hotelAvailabilityReq) {
      arrivalDate = hotelAvailabilityReq.getArrivalDate();
      departureDate = hotelAvailabilityReq.getDepartureDate();
    } else {
      throw new IllegalArgumentException("Object type not supported");
    }
  }

  private static boolean isNull(final String arrivalDate,
      final String departureDate) {
    return (StringUtils.isEmpty(arrivalDate)
        || StringUtils.isEmpty(departureDate));
  }

  private static boolean isValidArrivalAndDeparture(
      final String arrivalDate,
      final String departureDate) {
    try {
      log.trace("Validating arrival date is before the "
              + "departure date.... arrival - {} Departure - {}",
          arrivalDate,
          departureDate);

      final LocalDate arrivalDateObj = LocalDate.parse(
          arrivalDate, ISO_LOCAL_DATE);
      final LocalDate departureDateObj = LocalDate.parse(
          departureDate, ISO_LOCAL_DATE);

      if (arrivalDateObj.isBefore(LocalDate.now())
          || arrivalDateObj.equals(departureDateObj)
          || arrivalDateObj.isAfter(departureDateObj)) {
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
