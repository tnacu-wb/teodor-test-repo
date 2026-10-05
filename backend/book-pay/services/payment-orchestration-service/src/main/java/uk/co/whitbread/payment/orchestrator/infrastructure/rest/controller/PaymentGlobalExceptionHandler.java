package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BookingAlreadyConfirmedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BookingAlreadyPaidException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.GatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.PaymentMethodNotAvailableException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.SecureFieldsInitializationConflictException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ThreeDsAuthenticationFailedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionAlreadyAuthorizedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;

/**
 * Global exception handler for the Payment Orchestration Service.
 *
 * <p>Maps domain exceptions to standardised HTTP error responses with the format:
 * <pre>{"error": {"code": "...", "message": "..."}}</pre>
 *
 * <p>Ordered last on purpose. It ends in a {@code RuntimeException} catch-all, which also
 * matches exceptions that a more specific advice — {@link PaymentExceptionHandler} and its
 * {@link uk.co.whitbread.payment.orchestrator.domain.exceptions.PaymentInitializationException}
 * — exists to translate. {@code LOWEST_PRECEDENCE} alone does not guarantee that: an advice
 * with no {@code @Order} gets the same default value, and on a tie the winner is registration
 * order. The specific advice therefore carries an explicit {@code @Order(0)}, and this pair of
 * annotations only works as a pair — see the Javadoc on {@link PaymentExceptionHandler}.
 */
@RestControllerAdvice
@Order
@Slf4j
public class PaymentGlobalExceptionHandler {

  /**
   * Nested error body wrapper.
   */
  @Schema(description = "Standard error response wrapper")
  public record GlobalErrorResponse(
      @Schema(description = "Error details") ErrorDetail error) {}

  /**
   * Error detail containing machine-readable code and human-readable message.
   */
  @Schema(description = "Error detail")
  public record ErrorDetail(
      @Schema(description = "Machine-readable error code", example = "INVALID_REQUEST")
      String code,
      @Schema(description = "Human-readable error message", example = "basketId is required")
      String message) {}

  /**
   * Reports every field violation, not just the first.
   *
   * <p>A request that is wrong in three places is wrong in three places. Returning one at a
   * time forces the client into a fix-one-resubmit loop and makes a bad integration look like
   * three separate bugs. Field errors are sorted by field name so the same invalid request
   * always produces the same message, which keeps client-side assertions and log grouping
   * stable.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<GlobalErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    String message = ex.getFieldErrors().stream()
        .sorted(Comparator.comparing(FieldError::getField))
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .collect(Collectors.joining("; "));
    if (message.isEmpty()) {
      message = "Invalid request";
    }
    log.warn("Validation failed: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new GlobalErrorResponse(new ErrorDetail("INVALID_REQUEST", message)));
  }

  @ExceptionHandler(BasketNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleBasketNotFound(BasketNotFoundException ex) {
    log.warn("Basket not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new GlobalErrorResponse(new ErrorDetail("BASKET_NOT_FOUND", ex.getMessage())));
  }

  @ExceptionHandler(BookingAlreadyPaidException.class)
  public ResponseEntity<GlobalErrorResponse> handleBookingAlreadyPaid(
      BookingAlreadyPaidException ex) {
    log.warn("Booking already paid: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new GlobalErrorResponse(new ErrorDetail("BOOKING_ALREADY_PAID", ex.getMessage())));
  }

  @ExceptionHandler(BookingAlreadyConfirmedException.class)
  public ResponseEntity<GlobalErrorResponse> handleBookingAlreadyConfirmed(
      BookingAlreadyConfirmedException ex) {
    log.warn("Booking already confirmed: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new GlobalErrorResponse(
            new ErrorDetail("BOOKING_ALREADY_CONFIRMED", ex.getMessage())));
  }

  @ExceptionHandler(PaymentMethodNotAvailableException.class)
  public ResponseEntity<GlobalErrorResponse> handlePaymentMethodNotAvailable(
      PaymentMethodNotAvailableException ex) {
    log.warn("Payment method not available: {}", ex.getMessage());
    return ResponseEntity.status(422)
        .body(new GlobalErrorResponse(
            new ErrorDetail("PAYMENT_METHOD_NOT_AVAILABLE", ex.getMessage())));
  }

  @ExceptionHandler(SecureFieldsInitializationConflictException.class)
  public ResponseEntity<GlobalErrorResponse> handleSecureFieldsInitializationConflict(
      SecureFieldsInitializationConflictException ex) {
    log.warn("Secure Fields initialization conflict: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new GlobalErrorResponse(
            new ErrorDetail("SECURE_FIELDS_INITIALIZATION_CONFLICT", ex.getMessage())));
  }

  @ExceptionHandler(TransactionNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleTransactionNotFound(
      TransactionNotFoundException ex) {
    log.warn("Transaction not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new GlobalErrorResponse(
            new ErrorDetail("TRANSACTION_NOT_FOUND", ex.getMessage())));
  }

  @ExceptionHandler(TransactionAlreadyAuthorizedException.class)
  public ResponseEntity<GlobalErrorResponse> handleTransactionAlreadyAuthorized(
      TransactionAlreadyAuthorizedException ex) {
    log.warn("Transaction already authorized: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new GlobalErrorResponse(
            new ErrorDetail("TRANSACTION_ALREADY_AUTHORIZED", ex.getMessage())));
  }

  @ExceptionHandler(ThreeDsAuthenticationFailedException.class)
  public ResponseEntity<GlobalErrorResponse> handleThreeDsAuthenticationFailed(
      ThreeDsAuthenticationFailedException ex) {
    log.warn("3DS authentication failed: {}", ex.getMessage());
    return ResponseEntity.status(422)
        .body(new GlobalErrorResponse(
            new ErrorDetail("3DS_AUTHENTICATION_FAILED", ex.getMessage())));
  }

  @ExceptionHandler(GatewayException.class)
  public ResponseEntity<GlobalErrorResponse> handleGateway(GatewayException ex) {
    log.error("Downstream gateway error: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(new GlobalErrorResponse(new ErrorDetail("GATEWAY_ERROR", ex.getMessage())));
  }

  @ExceptionHandler(DatatransGatewayException.class)
  public ResponseEntity<GlobalErrorResponse> handleDatatransGateway(
      DatatransGatewayException ex) {
    log.error("Datatrans gateway error: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(new GlobalErrorResponse(new ErrorDetail("GATEWAY_ERROR", ex.getMessage())));
  }

  /**
   * Datatrans rejected our merchant credentials or permissions.
   *
   * <p>Deliberately answered with {@code 502} and a gateway-shaped code: the caller's own
   * authentication is fine, and no card was declined. Returning {@code 401} here would tell
   * clients to refresh a token that was never the problem. Logged at ERROR because only we can
   * fix it — the customer-facing message says nothing about a card or an issuer.
   */
  @ExceptionHandler(DatatransAuthenticationException.class)
  public ResponseEntity<GlobalErrorResponse> handleDatatransAuthentication(
      DatatransAuthenticationException ex) {
    log.error("Datatrans rejected merchant credentials: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(new GlobalErrorResponse(new ErrorDetail("GATEWAY_AUTHENTICATION_FAILED",
            PaymentErrorCode.GATEWAY_AUTHENTICATION_FAILED.getErrorMessage())));
  }

  @ExceptionHandler(ServiceUnavailableException.class)
  public ResponseEntity<GlobalErrorResponse> handleServiceUnavailable(
      ServiceUnavailableException ex) {
    log.error("Service unavailable: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(new GlobalErrorResponse(
            new ErrorDetail("SERVICE_UNAVAILABLE", ex.getMessage())));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<GlobalErrorResponse> handleMessageNotReadable(
      HttpMessageNotReadableException ex) {
    log.warn("Invalid request body: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new GlobalErrorResponse(new ErrorDetail("INVALID_REQUEST", "Invalid request body")));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<GlobalErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    log.warn("Invalid argument: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new GlobalErrorResponse(new ErrorDetail("INVALID_REQUEST", ex.getMessage())));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<GlobalErrorResponse> handleResponseStatusException(
      ResponseStatusException ex) {
    log.warn("Request failed: {}", ex.getMessage());
    String message = ex.getReason() != null ? ex.getReason() : "Request failed";
    String code = ex.getStatusCode().is5xxServerError() ? "SERVICE_UNAVAILABLE" : "INVALID_REQUEST";
    return ResponseEntity.status(ex.getStatusCode())
        .body(new GlobalErrorResponse(new ErrorDetail(code, message)));
  }

  /**
   * Catch-all for anything no handler above claimed.
   *
   * <p>Answered with {@code 500}, not {@code 503}. A {@code 503} is a promise that the fault is
   * downstream and transient — clients back off and retry it, and alerting reads it as a
   * dependency outage. An unrecognised {@code RuntimeException} is a bug in this service, so
   * saying {@code 503} sends callers into pointless retries and points the on-call engineer at
   * the wrong system. {@code 503} is reserved for
   * {@link ServiceUnavailableException}, which genuinely means a dependency is unreachable.
   *
   * <p>The response body is a fixed generic message: the exception's own message can carry
   * internal detail, and the stack trace belongs in the ERROR log, not in a client response.
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<GlobalErrorResponse> handleUnexpectedException(RuntimeException ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new GlobalErrorResponse(
            new ErrorDetail("INTERNAL_ERROR",
                "An unexpected error occurred. Please try again.")));
  }
}
