package uk.co.whitbread.payment.orchestrator.domain.model.payment.out;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Each value represents a distinct Datatrans integration type, not a payment instrument.
 *
 * <p>The enum discriminates how a payment is initialized and authorized with the Datatrans
 * gateway. Adding a new integration type (e.g. Google Pay, Apple Pay) means adding a new
 * enum constant here and a corresponding strategy implementation.
 */
@Schema(
    description = "Datatrans integration type, not a payment instrument. "
        + "NEW_CARD_WEB uses the Secure Fields integration (init then explicit authorize); "
        + "NEW_CARD_MOBILE uses the Mobile SDK integration (init then webhook/reconciliation).")
public enum PaymentMethod {

  /** Secure Fields integration — browser-side tokenization with explicit authorize call. */
  NEW_CARD_WEB,

  /** Mobile SDK integration — native SDK flow with webhook-based authorization. */
  NEW_CARD_MOBILE

  // Future integration types:
  // GOOGLE_PAY   — Mobile SDK / Payment Button integration (init + webhook)
  // APPLE_PAY    — Mobile SDK / Payment Button integration (init + webhook)
  // SAVED_CARD   — Direct authorize with alias (no init required)
}
