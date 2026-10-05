package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendStayDatesRequestDto;

@Slf4j
public class DateRangeValidator implements ConstraintValidator<DateRange, AmendStayDatesRequestDto> {

  private static final String FORMAT = "yyyy-MM-dd";

  @Override
  public boolean isValid(AmendStayDatesRequestDto value, ConstraintValidatorContext validatorContext) {
    if (value == null || value.getNewStartDate() == null || value.getNewEndDate() == null) {
      return true;
    }
    try {
      var arrivalDate =
              LocalDate.parse(value.getNewStartDate(), DateTimeFormatter.ofPattern(FORMAT));

      var departureDate =
              LocalDate.parse(value.getNewEndDate(), DateTimeFormatter.ofPattern(FORMAT));

      var currentDate = LocalDate.now();

      return !arrivalDate.isEqual(departureDate) && !arrivalDate.isAfter(departureDate)
          && (!arrivalDate.isBefore(currentDate));
    } catch (DateTimeParseException e) {
      log.error(
              "Error while trying to validate the arrival and departure date range for arrivalDate={} and "
                      + "departureDate={}",
              value.getNewStartDate(), value.getNewEndDate(), e);
      return false;
    }
  }
}
