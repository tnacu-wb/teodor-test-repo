package uk.co.whitbread.payment.orchestrator.domain.model.payment.in;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment.ReturnUrlValidator;

/**
 * Validates {@link PaymentInitRequest} instances based on their concrete subtype.
 *
 * <p>For {@link NewCardWebInitRequest}, validates that the returnUrl:
 * <ul>
 *   <li>Is present and non-blank (covered by {@code @NotBlank} on the field)</li>
 *   <li>Uses the HTTPS protocol</li>
 *   <li>Has a host in the configured allowlist</li>
 * </ul>
 *
 * <p>This validator uses {@code addPropertyNode("returnUrl")} to produce field-level
 * constraint violations, providing clear feedback to API clients about which field
 * failed validation.
 */
public class ConditionalValidationValidator
    implements ConstraintValidator<ConditionalValidation, PaymentInitRequest> {

  @Lazy
  @Autowired
  private ReturnUrlValidator returnUrlValidator;

  @Override
  public boolean isValid(PaymentInitRequest request, ConstraintValidatorContext context) {
    if (request == null) {
      return true;
    }

    if (request instanceof NewCardWebInitRequest webRequest) {
      return validateWebRequest(webRequest, context);
    }

    return true;
  }

  private boolean validateWebRequest(
      NewCardWebInitRequest webRequest, ConstraintValidatorContext context) {
    String returnUrl = webRequest.returnUrl();

    // @NotBlank handles null/blank — this validator only checks HTTPS + allowlist
    if (returnUrl == null || returnUrl.isBlank()) {
      return true;
    }

    if (!returnUrlValidator.isValidReturnUrl(returnUrl)) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "returnUrl must use HTTPS and belong to an allowed host")
          .addPropertyNode("returnUrl")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
