package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Created by Oleksandr Murha on 04/11/2016.
 */
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
      return false;
    }
  }
}
