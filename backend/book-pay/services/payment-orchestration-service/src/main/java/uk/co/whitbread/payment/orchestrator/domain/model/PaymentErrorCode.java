package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Framework-free error codes for payment processing.
 *
 * <p>HTTP status mapping is handled in {@code @ControllerAdvice}, not in this enum,
 * to keep the domain layer free of web framework dependencies.
 */
public enum PaymentErrorCode {
  BASKET_NOT_FOUND("No basket found for the given basketId"),
  PAYMENT_METHOD_NOT_AVAILABLE("Card payment not available for this hotel"),
  TRANSACTION_ALREADY_AUTHORIZED("Transaction has already been authorized"),
  BOOKING_ALREADY_PAID("Booking has already been paid"),
  GATEWAY_ERROR("Payment gateway error occurred"),
  /**
   * The payment gateway rejected this service's own merchant credentials or permissions.
   *
   * <p>An operational/configuration fault on our side, not a card problem — the message is
   * deliberately silent about the card and the issuer.
   */
  GATEWAY_AUTHENTICATION_FAILED(
      "Payment gateway configuration error — the payment could not be processed"),
  VALIDATION_FAILED("Request validation failed"),
  /**
   * The reservation's amount or currency cannot be safely converted for payment
   * (unsupported currency, missing values, or sub-minor-unit precision).
   */
  INVALID_AMOUNT("Reservation amount or currency cannot be processed for payment"),
  AUTHORIZATION_IN_PROGRESS("Authorization already in progress for this payment"),
  /**
   * Not a failure: the authorization was durably accepted by the workflow and is still running
   * when the caller's bounded wait elapsed.
   *
   * <p>The payment may still succeed. Callers must poll the payment status endpoint rather than
   * treat this as a decline or retry the authorization, which would risk a double charge.
   */
  AUTHORIZATION_PENDING(
      "Authorization is still processing — poll the payment status endpoint for the outcome"),
  TRANSACTION_EXPIRED("Payment transaction has expired"),
  /**
   * No payment workflow exists for the basket — authorize was called before init, or the
   * workflow already closed. The caller's error (a 404), never a gateway fault.
   */
  TRANSACTION_NOT_FOUND("No payment workflow found for the given basketId"),
  INVALID_TRANSACTION_STATE("Payment is not in a valid state for the requested operation"),
  TRANSACTION_MISMATCH(
      "Gateway transaction identifiers or authorized amount do not match this payment"),
  EXPIRED("Payment session has expired");

  private final String errorMessage;

  PaymentErrorCode(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * Returns a human-readable description of this error code.
   */
  public String getErrorMessage() {
    return errorMessage;
  }
}
