package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when a payment is attempted on a booking that already has a confirmation reference.
 */
public class BookingAlreadyConfirmedException extends RuntimeException {

  public BookingAlreadyConfirmedException(String message) {
    super(message);
  }

  public BookingAlreadyConfirmedException(String message, Throwable cause) {
    super(message, cause);
  }
}
