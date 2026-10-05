package uk.co.whitbread.payment.orchestrator.domain.exceptions;

import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;

/**
 * Thrown when payment initialization fails with a typed error code.
 *
 * <p>The associated {@link PaymentErrorCode} is mapped to an HTTP status in the
 * {@code @ControllerAdvice} exception handler, keeping this exception framework-free.
 */
public class PaymentInitializationException extends RuntimeException {

  private final PaymentErrorCode errorCode;

  public PaymentInitializationException(PaymentErrorCode errorCode) {
    super(errorCode.getErrorMessage());
    this.errorCode = errorCode;
  }

  public PaymentInitializationException(PaymentErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public PaymentInitializationException(PaymentErrorCode errorCode, Throwable cause) {
    super(errorCode.getErrorMessage(), cause);
    this.errorCode = errorCode;
  }

  public PaymentErrorCode getErrorCode() {
    return errorCode;
  }
}
