package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.PaymentInitializationException;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;

/**
 * Exception handler for payment initialization errors.
 *
 * <p>Maps {@link PaymentErrorCode} values to HTTP status codes, keeping the domain enum
 * framework-free while providing proper REST semantics at the infrastructure layer.
 *
 * <p>The explicit {@code @Order(0)} is load-bearing. Spring resolves an exception against
 * advices in order and stops at the <em>first advice with any matching handler</em> — it never
 * compares specificity across advices. {@link PaymentGlobalExceptionHandler} carries a
 * {@code RuntimeException} catch-all that also matches
 * {@link PaymentInitializationException}, and an advice with no {@code @Order} defaults to
 * {@code LOWEST_PRECEDENCE} — the same value Global declares. On that tie the winner is bean
 * registration order, which put Global first and turned every typed payment error into a
 * generic 500. This advice must therefore sort strictly before Global.
 */
@RestControllerAdvice
@Order(0)
@Slf4j
public class PaymentExceptionHandler {

  private static final Map<PaymentErrorCode, HttpStatus> ERROR_STATUS_MAP = Map.ofEntries(
      Map.entry(PaymentErrorCode.BASKET_NOT_FOUND, HttpStatus.NOT_FOUND),
      Map.entry(PaymentErrorCode.PAYMENT_METHOD_NOT_AVAILABLE, HttpStatus.UNPROCESSABLE_CONTENT),
      Map.entry(PaymentErrorCode.TRANSACTION_ALREADY_AUTHORIZED, HttpStatus.CONFLICT),
      Map.entry(PaymentErrorCode.BOOKING_ALREADY_PAID, HttpStatus.CONFLICT),
      Map.entry(PaymentErrorCode.GATEWAY_ERROR, HttpStatus.BAD_GATEWAY),
      // Our credentials failed at the gateway, not the caller's — 502, never 401/403.
      Map.entry(PaymentErrorCode.GATEWAY_AUTHENTICATION_FAILED, HttpStatus.BAD_GATEWAY),
      Map.entry(PaymentErrorCode.VALIDATION_FAILED, HttpStatus.BAD_REQUEST),
      Map.entry(PaymentErrorCode.INVALID_AMOUNT, HttpStatus.UNPROCESSABLE_CONTENT),
      Map.entry(PaymentErrorCode.AUTHORIZATION_IN_PROGRESS, HttpStatus.CONFLICT),
      Map.entry(PaymentErrorCode.TRANSACTION_EXPIRED, HttpStatus.GONE),
      // No workflow for the basket — the caller's error (authorize before init), never 502.
      Map.entry(PaymentErrorCode.TRANSACTION_NOT_FOUND, HttpStatus.NOT_FOUND),
      Map.entry(PaymentErrorCode.INVALID_TRANSACTION_STATE, HttpStatus.CONFLICT),
      Map.entry(PaymentErrorCode.TRANSACTION_MISMATCH, HttpStatus.CONFLICT),
      Map.entry(PaymentErrorCode.EXPIRED, HttpStatus.GONE)
  );

  @ExceptionHandler(PaymentInitializationException.class)
  public ResponseEntity<PaymentGlobalExceptionHandler.GlobalErrorResponse> handlePaymentInitialization(
      PaymentInitializationException ex) {
    PaymentErrorCode errorCode = ex.getErrorCode();
    HttpStatus status = ERROR_STATUS_MAP.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    log.warn("Payment initialization failed: code={}, message={}", errorCode, ex.getMessage());
    return ResponseEntity.status(status)
        .body(new PaymentGlobalExceptionHandler.GlobalErrorResponse(
            new PaymentGlobalExceptionHandler.ErrorDetail(errorCode.name(), ex.getMessage())));
  }
}
