package uk.co.whitbread.payment.orchestrator.domain.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ThreeDsAuthenticationFailedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionAlreadyAuthorizedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionMismatchException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.logic.AmountCalculator;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransCardInfo;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardWebInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;

import java.time.Duration;

/**
 * Strategy for the Secure Fields (web) payment integration.
 *
 * <p>Handles initialization via Datatrans Secure Fields API and authorization via the
 * event-inbox pattern (awaits {@code authorizeSignalReceived} from the workflow update).
 *
 * <p>This class is a plain deterministic Java class with no Spring dependencies. It is
 * instantiated directly by the strategy factory and runs within Temporal workflow context.
 *
 * <p>Authorization order matches {@link NewCardMobileStrategy}: the gateway authorize is
 * followed by the authorised-payment publish, and only a publish that succeeded moves the
 * payment to {@code AUTHORIZED}. The publish owns a retry horizon measured in minutes and
 * compensates to {@code FAILED} when it is exhausted, so the payment is not authorized until it
 * returns — a query made in that window reports {@code INITIALIZED}, which is the truth.
 *
 * <p>Exception handling uses fully qualified class name (FQCN) mapping to convert Temporal
 * {@link ApplicationFailure} wrappers back to typed {@link PaymentErrorCode} values.
 */
public class NewCardWebStrategy implements PaymentMethodStrategy {

  private static final Logger log = Workflow.getLogger(NewCardWebStrategy.class);

  private final AmountCalculator amountCalculator = new AmountCalculator();

  @Override
  public PaymentInitResult init(PaymentInitCommand command, PaymentActivities activities,
      WorkflowState state) {
    if (!(command instanceof NewCardWebInitCommand webCommand)) {
      return new PaymentInitResult(false, null,
          PaymentErrorCode.VALIDATION_FAILED,
          "Expected NewCardWebInitCommand but received " + command.getClass().getSimpleName());
    }

    try {
      // Step 1: Get reservation from Hotel Reservation Entity Service
      Reservation reservation = activities.getReservation(webCommand.basketId());

      // Step 2: Store bookingReference in workflow state for authorize/settle
      state.setBookingReference(reservation.bookingReference());
      state.setLanguage(webCommand.language());

      // Step 3: Validate payment methods
      // Its own stub: a 4xx from the Payment Method Entity Service is an answer, not an
      // outage, and must not be retried. See PaymentActivityStubs#paymentMethodActivities.
      PaymentMethodValidationResult paymentMethods = PaymentActivityStubs
          .paymentMethodActivities()
          .validatePaymentMethods(
              webCommand.basketId(), reservation.hotelId(),
              webCommand.country(), webCommand.language(),
              webCommand.userType(), webCommand.clientChannel());

      if (!paymentMethods.cardPaymentAvailable()) {
        return new PaymentInitResult(false, null,
            PaymentErrorCode.PAYMENT_METHOD_NOT_AVAILABLE,
            PaymentErrorCode.PAYMENT_METHOD_NOT_AVAILABLE.getErrorMessage());
      }

      // Step 4: Calculate amount in minor units
      long calculatedAmount = amountCalculator.toMinorUnits(
          reservation.totalCostOfStay(), reservation.currencyCode());

      // Step 5: Store amount, currency, and the basket correlation key in state
      state.setAmount(calculatedAmount);
      state.setCurrency(reservation.currencyCode());
      state.setBasketId(webCommand.basketId());

      // Step 6: Resolve merchant ID and build Datatrans Secure Fields request
      String merchantId = activities.resolveMerchantId(reservation.hotelId());
      state.setMerchantId(merchantId);

      var request = new DatatransSecureFieldsRequest(
          calculatedAmount, reservation.currencyCode(),
          webCommand.returnUrl(), merchantId, "POST");

      String transactionId = activities.initDatatransSecureFields(request);

      // Step 7: Store transactionId in workflow state for correlation
      state.setTransactionId(transactionId);

      // Step 8: Transition basket to PAY_PENDING before returning txnId to frontend
      activities.changeBasketStatus(state.getBookingReference(), "PAY_PENDING");

      // Step 9: Return success result
      return new PaymentInitResult(true, transactionId, null, null);

    } catch (Exception e) {
      PaymentErrorCode errorCode = PaymentFailureMapper.toErrorCode(e);
      log.warn("Secure Fields initialization failed [basketId={}, errorCode={}]",
          webCommand.basketId(), errorCode, e);
      return new PaymentInitResult(false, null, errorCode, errorCode.getErrorMessage());
    }
  }

  @Override
  public void awaitAuthorization(PaymentActivities activities, WorkflowState state) {
    // Wait for authorize signal or attempt failure via event-inbox pattern
    Workflow.await(() -> state.isAuthorizeSignalReceived() || state.isAttemptFailed());

    // If the attempt was externally failed (e.g. expiry), return early
    if (state.isAttemptFailed()) {
      return;
    }

    // Authorize-specific stub. Bounded retries are deliberate: an authorize whose response was
    // lost must be retried, and retrying is safe because the adapter resolves the gateway's
    // "already authorized" conflict against the transaction status and converges on the same
    // single authorization instead of charging a second time. Decisions — a decline, a 3-D
    // Secure failure, an unknown transaction, a confirmed transaction that does not match this
    // payment — are terminal and must not be retried.
    //
    // Rejected merchant credentials are listed too, though for a different reason: retrying is
    // harmless (a 401 moves no money) but pointless, because all three attempts carry the same
    // bad credentials. Failing fast keeps the customer's error prompt and leaves one clean
    // ERROR in the logs per attempt instead of three, which is what ops has to page on.
    PaymentActivities authorizeActivities = Workflow.newActivityStub(
        PaymentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(RetryOptions.newBuilder()
                .setMaximumAttempts(3)
                .setDoNotRetry(
                    TransactionNotFoundException.class.getName(),
                    TransactionAlreadyAuthorizedException.class.getName(),
                    ThreeDsAuthenticationFailedException.class.getName(),
                    TransactionMismatchException.class.getName(),
                    DatatransAuthenticationException.class.getName()
                )
                .build())
            .build()
    );

    try {
      // Step 1: Authorize with Datatrans
      DatatransAuthorizeResponse response = authorizeActivities.authorizeTransaction(
          state.getTransactionId(), state.getBookingReference(),
          state.getAmount(), state.getMerchantId());

      if (response == null) {
        throw new RuntimeException("Datatrans authorize returned null response");
      }

      // Step 2: Fetch card data from transaction status
      DatatransTransactionStatus txnStatus = activities.getTransactionStatus(
          state.getTransactionId(), state.getMerchantId());

      DatatransCardInfo card = txnStatus != null ? txnStatus.card() : null;
      String cardAlias = card != null ? card.alias() : null;
      String last4Digits = extractLast4Digits(card);
      String expiry = formatExpiry(card);
      String paymentMethod = txnStatus != null ? txnStatus.paymentMethod() : null;

      // Store card alias in state for downstream use
      state.setCardAlias(cardAlias);
      state.setAuthorizedAmount(state.getAmount());

      // Step 3: Publish PaymentAuthorisedEvent to Kafka. This is what starts the booking, so
      // an event that cannot be published means the authorization must be released — the
      // publisher compensates and fails the attempt, and there is nothing to report as success.
      boolean published = AuthorisedEventPublisher.publish(activities, state,
          state.getAmount(), paymentMethod, last4Digits, expiry);
      if (!published) {
        return;
      }

      // Step 4: Only now is the payment authorized. The publish above can retry for minutes and
      // can still compensate back to FAILED, so claiming AUTHORIZED before it returns would let
      // a query report a payment that is about to be cancelled, and would open the
      // bookingCompleted guard on an authorization that may not survive.
      state.setPaymentStatus(PaymentStatus.AUTHORIZED);
      state.setAuthorizeResult(new AuthorizeResult(true, null, null));

    } catch (Exception e) {
      PaymentErrorCode errorCode = PaymentFailureMapper.toErrorCode(e);
      log.warn("Authorization failed [transactionId={}, errorCode={}]",
          state.getTransactionId(), errorCode, e);
      state.setAuthorizeResult(new AuthorizeResult(
          false, errorCode, errorCode.getErrorMessage()));
      state.setPaymentStatus(PaymentStatus.FAILED);
      state.setAttemptFailed(true);
    }
  }

  @Override
  public PaymentMethod getSupportedMethod() {
    return PaymentMethod.NEW_CARD_WEB;
  }

  private String extractLast4Digits(DatatransCardInfo card) {
    if (card == null || card.masked() == null || card.masked().length() < 4) {
      return null;
    }
    return card.masked().substring(card.masked().length() - 4);
  }

  private String formatExpiry(DatatransCardInfo card) {
    if (card == null || card.expiryMonth() == null || card.expiryYear() == null) {
      return null;
    }
    return card.expiryMonth() + "/" + card.expiryYear();
  }
}
