package uk.co.whitbread.infrastructure.rest.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Set;

public class CountryFormatValidator implements ConstraintValidator<CountryFormat, String> {

  private static final Set<String> ISO_COUNTRIES = Set.of(Locale.getISOCountries());

  @Override
  public boolean isValid(String country, ConstraintValidatorContext validatorContext) {
    if (country == null) {
      return false;
    }
    return ISO_COUNTRIES.contains(country.toUpperCase(Locale.ROOT));
  }

}
