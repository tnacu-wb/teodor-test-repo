package uk.co.whitbread.payment.orchestrator.domain.ports.secondary;

import uk.co.whitbread.payment.orchestrator.domain.model.PaymentAuthorisedEvent;

/**
 * Secondary port for publishing payment domain events.
 *
 * <p>Defines the outbound contract for publishing payment events
 * to the messaging infrastructure after successful authorisation.
 */
public interface PaymentEventPublisherPort {

  /**
   * Publish a payment-authorised event.
   *
   * @param event the payment authorised event to publish
   */
  void publish(PaymentAuthorisedEvent event);
}
