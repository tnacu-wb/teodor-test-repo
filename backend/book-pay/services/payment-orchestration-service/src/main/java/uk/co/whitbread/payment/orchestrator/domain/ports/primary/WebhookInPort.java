package uk.co.whitbread.payment.orchestrator.domain.ports.primary;

import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;

/**
 * Primary port for handling inbound Datatrans webhooks.
 *
 * <p>Defines the inbound use cases for server-to-server payment
 * callbacks delivered by Datatrans after a payment event.
 */
public interface WebhookInPort {

  /**
   * Handle a Datatrans webhook by progressing the payment workflow correlated to the given basket.
   *
   * <p>Webhooks are gateway-scoped (not method-scoped): the same Datatrans endpoint serves both
   * web Secure Fields and Mobile SDK payment events. The workflow correlates the payload to a
   * running transaction via {@link WebhookPayload#transactionId()}.
   *
   * <p>Datatrans does not retry on a non-2xx response, so an unresolvable
   * webhook is acknowledged and logged rather than propagated as an error.
   *
   * @param basketId the basket identifier correlating the webhook to a payment workflow
   * @param payload the gateway-scoped Datatrans webhook payload
   */
  void handleWebhook(String basketId, WebhookPayload payload);
}
