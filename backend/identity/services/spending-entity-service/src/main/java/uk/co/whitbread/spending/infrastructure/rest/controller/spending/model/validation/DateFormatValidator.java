package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Oleksandr Murha on 04/11/2016.
 */
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
      YearMonth.parse(date, DateTimeFormatter.ofPattern(format));
      return true;
    } catch (DateTimeParseException e) {
      log.error("Error while trying to validate the date format for date={}", date, e);
      return false;
    }
  }
}