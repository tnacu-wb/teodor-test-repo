package uk.co.whitbread.basket.infrastructure.rest.controller.basket.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;

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
      return true;
    }
    try {
      LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
      return true;
    } catch (DateTimeParseException e) {
      log.error("Error while trying to validate date format for date={}", date, e);
      return false;
    }
  }
}