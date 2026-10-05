package uk.co.whitbread.payment.orchestrator.domain.model;

import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;

/**
 * Initialization command for the Secure Fields (web) integration.
 *
 * <p>Carries the 3-D Secure returnUrl that Datatrans redirects to after challenge flow.
 *
 * @param basketId      the basket identifier
 * @param returnUrl     the 3-D Secure redirect URL provided by the frontend (HTTPS only)
 * @param country       the country code (e.g. "gb", "de")
 * @param language      the language code (e.g. "en", "de")
 * @param userType      the user type (e.g. "LEISURE", "BUSINESS")
 * @param clientChannel the client channel (e.g. "PI", "BB")
 */
public record NewCardWebInitCommand(
    String basketId,
    String returnUrl,
    String country,
    String language,
    String userType,
    String clientChannel
) implements PaymentInitCommand {

  @Override
  public PaymentMethod paymentMethod() {
    return PaymentMethod.NEW_CARD_WEB;
  }
}
