package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when a payment is attempted on a booking that already has a completed payment.
 */
public class BookingAlreadyPaidException extends RuntimeException {

  public BookingAlreadyPaidException(String message) {
    super(message);
  }

  public BookingAlreadyPaidException(String message, Throwable cause) {
    super(message, cause);
  }
}
