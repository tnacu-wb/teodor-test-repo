package uk.co.whitbread.cdh.infrastructure.rest.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonthYearFormatValidator implements ConstraintValidator<MonthYearFormat, String> {

  private static final Pattern MONTH_YEAR_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])-\\d{4}$");

  @Override
  public boolean isValid(String monthYear, ConstraintValidatorContext validatorContext) {
    if (monthYear == null) {
      return true;
    }
    boolean isValid = MONTH_YEAR_PATTERN.matcher(monthYear).matches();
    if (!isValid) {
      log.error("Invalid month-year format for value={}", monthYear);
    }
    return isValid;
  }
}
