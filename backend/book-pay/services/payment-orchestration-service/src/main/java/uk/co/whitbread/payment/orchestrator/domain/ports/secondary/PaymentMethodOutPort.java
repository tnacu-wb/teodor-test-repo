package uk.co.whitbread.payment.orchestrator.domain.ports.secondary;

import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;

/**
 * Secondary port for payment method validation.
 *
 * <p>Defines the outbound contract for validating that card payment
 * is available for a given hotel via the Payment Method Entity Service.
 */
public interface PaymentMethodOutPort {

  /**
   * Validate that card payment is available for the given hotel and return supported card brands.
   *
   * @param basketReference the basket identifier
   * @param country the country code (e.g. "gb", "de")
   * @param language the language code (e.g. "en", "de")
   * @param userType the user type (e.g. "LEISURE", "BUSINESS")
   * @param clientChannel the client channel (e.g. "PI", "APPS_IOS")
   * @return validation result with card availability and supported brands
   */
  PaymentMethodValidationResult validatePaymentMethods(
      String basketReference, String country, String language, String userType, String clientChannel);
}
