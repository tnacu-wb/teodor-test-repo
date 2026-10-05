package uk.co.whitbread.payment.orchestrator.domain.workflow;

import io.temporal.failure.ApplicationFailure;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BookingAlreadyPaidException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.InvalidPaymentAmountException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.PaymentMethodNotAvailableException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ThreeDsAuthenticationFailedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionAlreadyAuthorizedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionMismatchException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;

/**
 * Single source of truth for mapping failures raised during payment processing to
 * {@link PaymentErrorCode} values, shared by every strategy.
 *
 * <p>Temporal wraps activity exceptions in {@code ActivityFailure} → {@link ApplicationFailure},
 * whose {@code getType()} carries the fully qualified class name of the original domain
 * exception. Exceptions thrown directly in workflow code (e.g. amount conversion) never cross
 * an activity boundary and are matched by instance instead.
 *
 * <p>Keeping the mapping here — rather than duplicated per strategy — guarantees the two flows
 * report the same error code for the same failure.
 */
public final class PaymentFailureMapper {

  // FQCN constants derived from the classes so renames can never silently break the mapping.
  private static final String BASKET_NOT_FOUND_FQCN =
      BasketNotFoundException.class.getName();
  private static final String PAYMENT_METHOD_NOT_AVAILABLE_FQCN =
      PaymentMethodNotAvailableException.class.getName();
  private static final String BOOKING_ALREADY_PAID_FQCN =
      BookingAlreadyPaidException.class.getName();
  private static final String THREE_DS_AUTHENTICATION_FAILED_FQCN =
      ThreeDsAuthenticationFailedException.class.getName();
  private static final String TRANSACTION_NOT_FOUND_FQCN =
      TransactionNotFoundException.class.getName();
  private static final String TRANSACTION_ALREADY_AUTHORIZED_FQCN =
      TransactionAlreadyAuthorizedException.class.getName();
  private static final String TRANSACTION_MISMATCH_FQCN =
      TransactionMismatchException.class.getName();
  private static final String DATATRANS_AUTHENTICATION_FQCN =
      DatatransAuthenticationException.class.getName();

  private PaymentFailureMapper() {
  }

  /**
   * Maps a failure from payment processing to its {@link PaymentErrorCode}.
   *
   * @param throwable the exception thrown by a Temporal activity call or directly in
   *                  workflow code
   * @return the corresponding error code; {@link PaymentErrorCode#GATEWAY_ERROR} when the
   *         failure type is unrecognized
   */
  static PaymentErrorCode toErrorCode(Throwable throwable) {
    // Thrown directly in workflow code (AmountCalculator), never via an activity,
    // so it is matched by instance rather than by ApplicationFailure type.
    if (throwable instanceof InvalidPaymentAmountException) {
      return PaymentErrorCode.INVALID_AMOUNT;
    }
    return fromFailureType(extractFailureType(throwable));
  }

  /**
   * Maps an {@link ApplicationFailure} type string (the FQCN of the original domain exception)
   * to its {@link PaymentErrorCode}. Shared by the workflow strategies and the Temporal
   * adapter so both report the same code for the same failure.
   *
   * @param fqcn the fully qualified class name from {@code ApplicationFailure.getType()}
   * @return the corresponding error code; {@link PaymentErrorCode#GATEWAY_ERROR} when the
   *         type is null or unrecognized
   */
  public static PaymentErrorCode fromFailureType(String fqcn) {
    if (fqcn == null) {
      return PaymentErrorCode.GATEWAY_ERROR;
    }
    if (BASKET_NOT_FOUND_FQCN.equals(fqcn)) {
      return PaymentErrorCode.BASKET_NOT_FOUND;
    }
    if (PAYMENT_METHOD_NOT_AVAILABLE_FQCN.equals(fqcn)) {
      return PaymentErrorCode.PAYMENT_METHOD_NOT_AVAILABLE;
    }
    if (BOOKING_ALREADY_PAID_FQCN.equals(fqcn)) {
      return PaymentErrorCode.BOOKING_ALREADY_PAID;
    }
    if (TRANSACTION_NOT_FOUND_FQCN.equals(fqcn)) {
      // Datatrans 404: the transaction does not exist or has expired at the gateway.
      return PaymentErrorCode.TRANSACTION_EXPIRED;
    }
    if (TRANSACTION_ALREADY_AUTHORIZED_FQCN.equals(fqcn)) {
      return PaymentErrorCode.TRANSACTION_ALREADY_AUTHORIZED;
    }
    if (TRANSACTION_MISMATCH_FQCN.equals(fqcn)) {
      return PaymentErrorCode.TRANSACTION_MISMATCH;
    }
    if (DATATRANS_AUTHENTICATION_FQCN.equals(fqcn)) {
      // Our merchant credentials, not the customer's card — the code must not blame the card.
      return PaymentErrorCode.GATEWAY_AUTHENTICATION_FAILED;
    }
    if (THREE_DS_AUTHENTICATION_FAILED_FQCN.equals(fqcn)) {
      return PaymentErrorCode.GATEWAY_ERROR;
    }
    return PaymentErrorCode.GATEWAY_ERROR;
  }

  /**
   * Extracts the failure type from a Temporal exception cause chain.
   *
   * <p>Walks the cause chain looking for an {@link ApplicationFailure} and returns its
   * {@code getType()} value (the FQCN of the original domain exception). If none is found,
   * returns the fully qualified class name of the deepest cause so that directly thrown
   * exceptions map by the same FQCN convention.
   *
   * @param throwable the exception to inspect
   * @return the fully qualified class name of the underlying failure type
   */
  static String extractFailureType(Throwable throwable) {
    Throwable current = throwable;
    while (current != null) {
      if (current instanceof ApplicationFailure applicationFailure) {
        String type = applicationFailure.getType();
        if (type != null && !type.isBlank()) {
          return type;
        }
      }
      current = current.getCause();
    }
    Throwable deepest = throwable;
    while (deepest.getCause() != null) {
      deepest = deepest.getCause();
    }
    return deepest.getClass().getName();
  }

  /**
   * Checks whether the cause chain contains an {@link ApplicationFailure} with the given
   * expected type's fully qualified class name, or a direct instance of the expected type.
   */
  static boolean hasFailureType(Throwable failure, Class<? extends Throwable> expectedType) {
    Throwable current = failure;
    while (current != null) {
      if (expectedType.isInstance(current)) {
        return true;
      }
      if (current instanceof ApplicationFailure applicationFailure
          && expectedType.getName().equals(applicationFailure.getType())) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }
}
