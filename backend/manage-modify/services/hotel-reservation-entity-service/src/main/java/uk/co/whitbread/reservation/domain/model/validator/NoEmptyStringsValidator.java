package uk.co.whitbread.reservation.domain.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class NoEmptyStringsValidator implements ConstraintValidator<NoEmptyStrings, List<String>> {

  @Override
  public void initialize(NoEmptyStrings constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(List<String> value, ConstraintValidatorContext constraintValidatorContext) {
    if (value == null) {
      return true;
    }
    return value.stream().noneMatch(String::isEmpty);
  }
}
