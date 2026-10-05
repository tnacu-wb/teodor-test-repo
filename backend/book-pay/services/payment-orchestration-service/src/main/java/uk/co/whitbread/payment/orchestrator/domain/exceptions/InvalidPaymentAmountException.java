package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when a reservation amount or currency cannot be safely converted for payment.
 *
 * <p>Raised by the amount conversion when the currency is missing or not supported by the
 * business (only GBP and EUR are accepted), the amount is missing or negative, or the amount
 * carries more precision than the currency's minor unit allows (e.g. 86.005 GBP). Guessing a
 * currency exponent or silently rounding money would risk charging the wrong amount, so the
 * conversion fails instead.
 *
 * <p>This indicates invalid upstream reservation data, not a gateway fault — it is thrown in
 * workflow code before any gateway call and is never retried.
 */
public class InvalidPaymentAmountException extends RuntimeException {

  public InvalidPaymentAmountException(String message) {
    super(message);
  }

  public InvalidPaymentAmountException(String message, Throwable cause) {
    super(message, cause);
  }
}
