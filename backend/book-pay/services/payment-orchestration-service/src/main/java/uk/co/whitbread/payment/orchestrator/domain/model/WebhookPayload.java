package uk.co.whitbread.payment.orchestrator.domain.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Gateway-scoped webhook payload hierarchy.
 *
 * <p>Webhooks are discriminated by gateway type, not by payment method. The workflow correlates
 * a webhook to its transaction using {@link #transactionId()} — it never needs to know which
 * payment method strategy is running. This decouples signal handling from strategy routing and
 * keeps the event-inbox pattern generic.
 *
 * <p>The {@code gateway} discriminator property is injected by the controller layer when
 * constructing the domain payload from the raw gateway-specific HTTP request body.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "gateway")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DatatransWebhookPayload.class, name = "DATATRANS")
})
public sealed interface WebhookPayload permits DatatransWebhookPayload {

  /**
   * The gateway transaction identifier used for workflow correlation and replay-attack guard.
   *
   * @return the transaction identifier from the payment gateway
   */
  String transactionId();
}
