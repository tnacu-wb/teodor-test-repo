package uk.co.whitbread.piba.registration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

  @Override
  public void initialize(NullOrNotBlank constraintAnnotation) {
    // No initialization needed
  }

  @Override
  public boolean isValid(
      String value,
      ConstraintValidatorContext context
  ) {

    if (value == null) {
      return true;
    }

    return !value.isBlank();
  }
}
