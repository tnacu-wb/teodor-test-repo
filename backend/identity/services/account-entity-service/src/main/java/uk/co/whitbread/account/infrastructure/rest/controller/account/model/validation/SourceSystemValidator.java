package uk.co.whitbread.account.infrastructure.rest.controller.account.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

public class SourceSystemValidator implements ConstraintValidator<ValidateSourceSystem, String> {

  private List<String> acceptedValues;

  @Override
  public void initialize(ValidateSourceSystem annotation) {
    acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
        .map(Enum::name)
        .toList();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    context.buildConstraintViolationWithTemplate(
        String.format("Supplied value is not one of %s.", acceptedValues)).addConstraintViolation();

    if (value == null) {
      return true;
    }

    return acceptedValues.contains(value);
  }
}
