package uk.co.whitbread.payment.orchestrator.domain.model;

import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;

/**
 * Initialization command for the Mobile SDK integration.
 *
 * <p>Does not carry a returnUrl — the native SDK handles 3-D Secure challenges in-app
 * and Datatrans posts results via webhook callback.
 *
 * <p>The {@code webhookUrl} and {@code reconciliation} fields are infrastructure-level
 * settings injected by the adapter layer before the command reaches the workflow. Controllers
 * create instances without these fields; the adapter enriches them.
 *
 * @param basketId               the basket identifier
 * @param country                the country code (e.g. "gb", "de")
 * @param language               the language code (e.g. "en", "de")
 * @param userType               the user type (e.g. "LEISURE", "BUSINESS")
 * @param clientChannel          the client channel (e.g. "APPS_IOS", "APPS_ANDROID")
 * @param webhookUrl             the public Datatrans callback URL (null when not configured)
 * @param reconciliation         reconciliation polling settings (null when not configured)
 */
public record NewCardMobileInitCommand(
    String basketId,
    String country,
    String language,
    String userType,
    String clientChannel,
    String webhookUrl,
    MobileSdkReconciliationSettings reconciliation
) implements PaymentInitCommand {

  /**
   * Convenience constructor for controller-layer creation (without infrastructure settings).
   */
  public NewCardMobileInitCommand(String basketId, String country, String language,
      String userType, String clientChannel) {
    this(basketId, country, language, userType, clientChannel, null, null);
  }

  @Override
  public PaymentMethod paymentMethod() {
    return PaymentMethod.NEW_CARD_MOBILE;
  }
}
