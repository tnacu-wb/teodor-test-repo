package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class ArrivalDateValidator implements ConstraintValidator<ArrivalDate, String> {

  private String format;

  @Override
  public void initialize(ArrivalDate arrivalDateAnnotation) {
    format = arrivalDateAnnotation.format();
  }

  @Override
  public boolean isValid(String date, ConstraintValidatorContext validatorContext) {
    log.debug("Executing \' ArrivalDateValidator \' for :{}", date);
    return isValidDateFormat(date) && isValidArrivalDate(date);
  }

  public boolean isValidDateFormat(String date) {
    if (date == null) {
      return false;
    }
    try {
      LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
      return true;
    } catch (DateTimeParseException e) {
      log.error("Exception while parsing the date, received exception - {}", e.getMessage());
      return false;
    }
  }

  private boolean isValidArrivalDate(String date) {
    return !LocalDate.parse(date).isBefore(LocalDate.now());
  }
}