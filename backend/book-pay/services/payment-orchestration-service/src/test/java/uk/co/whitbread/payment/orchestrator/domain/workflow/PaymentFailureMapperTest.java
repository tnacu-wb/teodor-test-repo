package uk.co.whitbread.payment.orchestrator.domain.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import io.temporal.api.enums.v1.RetryState;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
 * Tests for {@link PaymentFailureMapper}, the single mapping from processing failures to
 * {@link PaymentErrorCode} values shared by both strategies.
 */
class PaymentFailureMapperTest {

  private static ActivityFailure wrapInActivityFailure(ApplicationFailure appFailure) {
    return new ActivityFailure(
        "Activity failed", 0L, 0L, "authorizeTransaction", "activityId",
        RetryState.RETRY_STATE_MAXIMUM_ATTEMPTS_REACHED, "worker-1", appFailure);
  }

  private static ApplicationFailure appFailureOf(Class<? extends Throwable> type) {
    return ApplicationFailure.newFailure(type.getSimpleName(), type.getName());
  }

  @Nested
  class ExtractFailureType {

    @Test
    void directApplicationFailure_returnsFqcnType() {
      String result = PaymentFailureMapper.extractFailureType(
          appFailureOf(BasketNotFoundException.class));

      assertThat(result).isEqualTo(BasketNotFoundException.class.getName());
    }

    @Test
    void activityFailureWrappingApplicationFailure_extractsFqcn() {
      ActivityFailure activityFailure = wrapInActivityFailure(
          appFailureOf(TransactionNotFoundException.class));

      String result = PaymentFailureMapper.extractFailureType(activityFailure);

      assertThat(result).isEqualTo(TransactionNotFoundException.class.getName());
    }

    @Test
    void deeplyNestedCauseChain_findsApplicationFailure() {
      RuntimeException outer = new RuntimeException("outer",
          new IllegalStateException("middle",
              wrapInActivityFailure(appFailureOf(BookingAlreadyPaidException.class))));

      String result = PaymentFailureMapper.extractFailureType(outer);

      assertThat(result).isEqualTo(BookingAlreadyPaidException.class.getName());
    }

    @Test
    void noApplicationFailureInChain_fallsBackToDeepestCauseFqcn() {
      RuntimeException outer = new RuntimeException("outer",
          new IllegalArgumentException("deepest"));

      String result = PaymentFailureMapper.extractFailureType(outer);

      assertThat(result).isEqualTo(IllegalArgumentException.class.getName());
    }

    @Test
    void plainExceptionWithNoCause_returnsOwnFqcn() {
      String result = PaymentFailureMapper.extractFailureType(
          new IllegalStateException("plain"));

      assertThat(result).isEqualTo(IllegalStateException.class.getName());
    }

    @Test
    void applicationFailureWithBlankType_isSkippedInFavourOfFallback() {
      ApplicationFailure blankType = ApplicationFailure.newFailure("no type", "  ");

      String result = PaymentFailureMapper.extractFailureType(blankType);

      assertThat(result).isEqualTo(ApplicationFailure.class.getName());
    }
  }

  @Nested
  class HasFailureType {

    @Test
    void directInstanceOfExpectedType_returnsTrue() {
      boolean result = PaymentFailureMapper.hasFailureType(
          new TransactionNotFoundException("not found"), TransactionNotFoundException.class);

      assertThat(result).isTrue();
    }

    @Test
    void applicationFailureWithMatchingFqcn_returnsTrue() {
      boolean result = PaymentFailureMapper.hasFailureType(
          appFailureOf(TransactionNotFoundException.class), TransactionNotFoundException.class);

      assertThat(result).isTrue();
    }

    @Test
    void activityFailureWrappingMatchingApplicationFailure_returnsTrue() {
      boolean result = PaymentFailureMapper.hasFailureType(
          wrapInActivityFailure(appFailureOf(TransactionNotFoundException.class)),
          TransactionNotFoundException.class);

      assertThat(result).isTrue();
    }

    @Test
    void noMatchingTypeInChain_returnsFalse() {
      boolean result = PaymentFailureMapper.hasFailureType(
          wrapInActivityFailure(appFailureOf(BasketNotFoundException.class)),
          TransactionNotFoundException.class);

      assertThat(result).isFalse();
    }

    @Test
    void plainExceptionWithoutCause_returnsFalse() {
      boolean result = PaymentFailureMapper.hasFailureType(
          new RuntimeException("no cause"), TransactionNotFoundException.class);

      assertThat(result).isFalse();
    }
  }

  @Nested
  class ToErrorCode {

    @Test
    void basketNotFound_mapsToBasketNotFound() {
      assertThat(PaymentFailureMapper.toErrorCode(
          wrapInActivityFailure(appFailureOf(BasketNotFoundException.class))))
          .isEqualTo(PaymentErrorCode.BASKET_NOT_FOUND);
    }

    @Test
    void paymentMethodNotAvailable_mapsToPaymentMethodNotAvailable() {
      assertThat(PaymentFailureMapper.toErrorCode(
          wrapInActivityFailure(appFailureOf(PaymentMethodNotAvailableException.class))))
          .isEqualTo(PaymentErrorCode.PAYMENT_METHOD_NOT_AVAILABLE);
    }

    @Test
    void bookingAlreadyPaid_mapsToBookingAlreadyPaid() {
      assertThat(PaymentFailureMapper.toErrorCode(
          wrapInActivityFailure(appFailureOf(BookingAlreadyPaidException.class))))
          .isEqualTo(PaymentErrorCode.BOOKING_ALREADY_PAID);
    }

    @Test
    void transactionNotFound_mapsToTransactionExpired() {
      // Datatrans 404: the transaction is gone or expired at the gateway — the same code
      // must come back regardless of which strategy hit it.
      assertThat(PaymentFailureMapper.toErrorCode(
          wrapInActivityFailure(appFailureOf(TransactionNotFoundException.class))))
          .isEqualTo(PaymentErrorCode.TRANSACTION_EXPIRED);
    }

    @Test
    void transactionAlreadyAuthorized_mapsToTransactionAlreadyAuthorized() {
      assertThat(PaymentFailureMapper.toErrorCode(
          wrapInActivityFailure(appFailureOf(TransactionAlreadyAuthorizedException.class))))
          .isEqualTo(PaymentErrorCode.TRANSACTION_ALREADY_AUTHORIZED);
    }

    @Test
    void transactionMismatch_mapsToTransactionMismatch() {
      assertThat(PaymentFailureMapper.toErrorCode(
          wrapInActivityFailure(appFailureOf(TransactionMismatchException.class))))
          .isEqualTo(PaymentErrorCode.TRANSACTION_MISMATCH);
    }

    @Test
    void datatransAuthentication_mapsToGatewayAuthenticationFailed() {
      assertThat(PaymentFailureMapper.toErrorCode(
          wrapInActivityFailure(appFailureOf(DatatransAuthenticationException.class))))
          .isEqualTo(PaymentErrorCode.GATEWAY_AUTHENTICATION_FAILED);
    }

    @Test
    void threeDsAuthenticationFailed_mapsToGatewayError() {
      assertThat(PaymentFailureMapper.toErrorCode(
          wrapInActivityFailure(appFailureOf(ThreeDsAuthenticationFailedException.class))))
          .isEqualTo(PaymentErrorCode.GATEWAY_ERROR);
    }

    @Test
    void invalidPaymentAmount_thrownDirectlyInWorkflowCode_mapsToInvalidAmount() {
      assertThat(PaymentFailureMapper.toErrorCode(
          new InvalidPaymentAmountException("unsupported currency")))
          .isEqualTo(PaymentErrorCode.INVALID_AMOUNT);
    }

    @Test
    void unknownFailureType_mapsToGatewayError() {
      assertThat(PaymentFailureMapper.toErrorCode(
          wrapInActivityFailure(ApplicationFailure.newFailure("boom", "com.example.Unknown"))))
          .isEqualTo(PaymentErrorCode.GATEWAY_ERROR);
    }

    @Test
    void plainRuntimeException_mapsToGatewayError() {
      assertThat(PaymentFailureMapper.toErrorCode(new RuntimeException("unexpected")))
          .isEqualTo(PaymentErrorCode.GATEWAY_ERROR);
    }
  }
}
