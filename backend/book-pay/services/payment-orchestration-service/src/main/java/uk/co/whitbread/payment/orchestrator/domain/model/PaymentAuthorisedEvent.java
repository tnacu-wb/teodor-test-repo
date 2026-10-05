package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Event published to the {@code payment-authorised} Kafka topic when a payment is
 * successfully authorised. Consumed by the Basket Service to drive the
 * booking-confirmation choreography.
 *
 * <p>This record intentionally excludes raw PAN, CVV, or any PCI-sensitive data.
 * Consumers MUST tolerate unknown fields for forward compatibility.
 *
 * @param basketId         the basket identifier for this payment
 * @param transactionId    the Datatrans transaction identifier
 * @param paymentProvider  the payment provider (e.g. "datatrans")
 * @param paymentMethod    the payment method code (e.g. "VIS", "ECA", "AMX")
 * @param cardAlias        the tokenised card alias (may be null if not available)
 * @param last4Digits      the last four digits of the card number
 * @param expiry           the card expiry formatted as MM/YY
 * @param authorizedAmount the authorized amount in minor units (e.g. pence); always present
 * @param currency         the ISO 4217 currency code (e.g. "GBP")
 * @param paymentOption    the payment option (e.g. PAY_NOW)
 * @param paymentStatus    the payment status at publication; always {@code "AUTHORIZED"} — the
 *                         event exists only for that transition, the field just says so
 *                         explicitly for consumers
 * @param language         the customer's language as sent by the frontend on init
 *                         (e.g. "en", "de"); may be null for histories recorded before the
 *                         field existed
 */
public record PaymentAuthorisedEvent(
    String basketId,
    String transactionId,
    String paymentProvider,
    String paymentMethod,
    String cardAlias,
    String last4Digits,
    String expiry,
    long authorizedAmount,
    String currency,
    PaymentOption paymentOption,
    String paymentStatus,
    String language
) {}
