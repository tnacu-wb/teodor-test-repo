package uk.co.whitbread.rules.agent.infrastructure.rest.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeFormatValidator implements ConstraintValidator<DateTimeFormat, String> {

  private String format;

  @Override
  public void initialize(DateTimeFormat dateFormatAnnotation) {
    format = dateFormatAnnotation.value();
  }

  @Override
  public boolean isValid(String date, ConstraintValidatorContext validatorContext) {
    if (date == null) {
      return true;
    }
    try {
      LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
      return true;
    } catch (DateTimeParseException e) {
      log.error("Error while trying to validate date format for date={}", date);
      return false;
    }
  }
}
