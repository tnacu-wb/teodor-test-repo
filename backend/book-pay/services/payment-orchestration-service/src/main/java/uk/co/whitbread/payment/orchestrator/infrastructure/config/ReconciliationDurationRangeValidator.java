package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Duration;

/** Validates that the reconciliation deadline is not before the initial delay. */
public class ReconciliationDurationRangeValidator
    implements ConstraintValidator<ValidReconciliationDurationRange,
    DatatransReconciliationProperties> {

  private static final String MAX_DURATION_KEY =
      "integrations.datatrans.reconciliation.max-duration";
  private static final String INITIAL_DELAY_KEY =
      "integrations.datatrans.reconciliation.initial-delay";

  @Override
  public boolean isValid(
      DatatransReconciliationProperties properties, ConstraintValidatorContext context) {
    if (properties == null) {
      return true;
    }

    Duration initialDelay = properties.getInitialDelay();
    Duration maxDuration = properties.getMaxDuration();
    if (initialDelay == null || maxDuration == null
        || maxDuration.compareTo(initialDelay) >= 0) {
      return true;
    }

    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(
            MAX_DURATION_KEY + " must be >= " + INITIAL_DELAY_KEY)
        .addPropertyNode("maxDuration")
        .addConstraintViolation();
    return false;
  }
}
