package uk.co.whitbread.token.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidProviderId annotation. Validates that provider ID contains only alphanumeric
 * characters, hyphens, and underscores.
 */
public class ProviderIdValidator implements ConstraintValidator<ValidProviderId, String> {

  private static final String VALID_PROVIDER_PATTERN = "^[a-zA-Z0-9_-]+$";

  @Override
  public boolean isValid(String providerId, ConstraintValidatorContext context) {
    if (providerId == null) {
      return false;
    }
    return providerId.matches(VALID_PROVIDER_PATTERN);
  }
}
