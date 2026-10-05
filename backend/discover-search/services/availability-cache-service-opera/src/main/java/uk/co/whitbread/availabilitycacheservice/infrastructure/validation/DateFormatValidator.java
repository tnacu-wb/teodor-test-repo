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
public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

  private String format;

  @Override
  public void initialize(DateFormat dateFormatAnnotation) {

    format = dateFormatAnnotation.value();
  }

  @Override
  public boolean isValid(String date, ConstraintValidatorContext validatorContext) {

    if (date == null) {
      log.error("date is null, and dateFormatValidator returns null");
      return false;
    }
    try {
      log.debug("Executing \' DateFormat Validation \' for :{}", date);
      LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
      return true;
    } catch (DateTimeParseException e) {
      log.error("Exception while parsing the date, received exception - {}", e.getMessage());
      return false;
    }
  }
}
