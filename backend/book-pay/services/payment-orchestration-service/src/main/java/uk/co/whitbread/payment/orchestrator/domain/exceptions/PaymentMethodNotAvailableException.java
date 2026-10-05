package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when card payment is not available for the hotel associated with the booking.
 */
public class PaymentMethodNotAvailableException extends RuntimeException {

  public PaymentMethodNotAvailableException(String message) {
    super(message);
  }

  public PaymentMethodNotAvailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
