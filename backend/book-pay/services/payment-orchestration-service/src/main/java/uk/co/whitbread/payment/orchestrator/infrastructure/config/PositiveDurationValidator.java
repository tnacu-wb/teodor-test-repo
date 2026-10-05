package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Duration;

/** Bean Validation validator for positive {@link Duration} values. */
public class PositiveDurationValidator implements ConstraintValidator<PositiveDuration, Duration> {

  @Override
  public boolean isValid(Duration value, ConstraintValidatorContext context) {
    return value != null && !value.isZero() && !value.isNegative();
  }
}
