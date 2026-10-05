package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Payment option indicating how the guest chose to pay for the booking.
 *
 * <p>Included in the {@link PaymentAuthorisedEvent} so downstream consumers
 * (e.g. Basket Service) can determine the payment flow context.
 */
public enum PaymentOption {

  /** Guest pays the full amount now at booking time. */
  PAY_NOW
}
