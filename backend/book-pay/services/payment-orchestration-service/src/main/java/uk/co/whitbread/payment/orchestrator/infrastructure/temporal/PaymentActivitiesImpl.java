package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.model.BasketStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentAuthorisedEvent;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentOption;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.DatatransOutPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.MerchantIdResolver;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentEventPublisherPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentMethodOutPort;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentActivities;

/**
 * Infrastructure implementation of {@link PaymentActivities}.
 *
 * <p>Delegates each Temporal activity to the appropriate secondary port,
 * bridging the workflow layer with external service adapters.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentActivitiesImpl implements PaymentActivities {

  private final BasketOutPort basketOutPort;
  private final PaymentMethodOutPort paymentMethodOutPort;
  private final DatatransOutPort datatransOutPort;
  private final PaymentEventPublisherPort paymentEventPublisher;
  private final MerchantIdResolver merchantIdResolver;

  @Override
  public DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno,
      long amount, String merchantId) {
    log.info("Authorizing transaction transactionId={}, merchantId={}", transactionId, merchantId);
    return datatransOutPort.authorizeTransaction(transactionId, refno, amount, merchantId);
  }

  @Override
  public DatatransTransactionStatus getTransactionStatus(String transactionId,
      String merchantId) {
    return datatransOutPort.getTransactionStatus(transactionId, merchantId);
  }

  @Override
  public Reservation getReservation(String basketId) {
    log.info("Retrieving reservation for basketId={}", basketId);
    return basketOutPort.getReservation(basketId);
  }

  @Override
  public PaymentMethodValidationResult validatePaymentMethods(
      String basketReference, String hotelId, String country, String language,
      String userType, String clientChannel) {
    log.info("Validating payment methods for basketReference={}, hotelId={},"
            + " country={}, clientChannel={}",
        basketReference, hotelId, country, clientChannel);
    return paymentMethodOutPort.validatePaymentMethods(
        basketReference, country, language, userType, clientChannel);
  }

  @Override
  public String initDatatransSecureFields(DatatransSecureFieldsRequest request) {
    log.info("Initializing Datatrans Secure Fields with amount={}, currency={}",
        request.amount(), request.currency());
    return datatransOutPort.initSecureFields(request);
  }

  @Override
  public String initMobileSdkTransaction(DatatransMobileSdkRequest request) {
    log.info("Initializing Mobile SDK transaction with amount={}, currency={}, refno={}",
        request.amount(), request.currency(), request.refno());
    try {
      return datatransOutPort.initMobileSdk(request);
    } catch (DatatransGatewayException | ServiceUnavailableException e) {
      throw e;
    } catch (Exception e) {
      throw new ServiceUnavailableException(
          "Unexpected error during Mobile SDK initialization: " + e.getMessage(), e);
    }
  }

  @Override
  public void settleTransaction(String transactionId, long amount, String currency,
      String refno, String merchantId) {
    log.info("Settling transaction transactionId={}, merchantId={}", transactionId, merchantId);
    datatransOutPort.settleTransaction(transactionId, amount, currency, refno, merchantId);
  }

  @Override
  public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
      String cardAlias, long authorizedAmount, String currency, String paymentMethod,
      String last4Digits, String expiry, PaymentOption paymentOption, String language) {
    log.info("Publishing PaymentAuthorisedEvent [basketId={}, transactionId={}, cardAlias={}]",
        basketId, transactionId, cardAlias != null ? "present" : "absent");

    var event = new PaymentAuthorisedEvent(
        basketId,
        transactionId,
        "datatrans",
        paymentMethod,
        cardAlias,
        last4Digits,
        expiry,
        authorizedAmount,
        currency,
        paymentOption,
        // A literal, not PaymentStatus.AUTHORIZED.name(): this is a wire contract, and a
        // rename of the internal enum must not silently change what consumers receive. The
        // event exists only for the AUTHORIZED transition; the field says so explicitly.
        "AUTHORIZED",
        language
    );

    paymentEventPublisher.publish(event);
  }

  @Override
  public String resolveMerchantId(String hotelCode) {
    log.info("Resolving merchant ID for hotelCode={}", hotelCode);
    return merchantIdResolver.resolveMerchantId(hotelCode);
  }

  @Override
  public void cancelTransaction(String transactionId, String merchantId) {
    log.info("Cancelling transaction transactionId={}, merchantId={}", transactionId, merchantId);
    datatransOutPort.cancelTransaction(transactionId, merchantId);
    log.info("Successfully cancelled transaction transactionId={}, merchantId={}",
        transactionId, merchantId);
  }

  @Override
  public BasketStatus getBasketStatus(String basketId) {
    log.info("Reading basket status [basketId={}]", basketId);
    return basketOutPort.getBasketStatus(basketId);
  }

  @Override
  public void changeBasketStatus(String bookingReference, String status) {
    log.info("Changing basket status [bookingReference={}, status={}]",
        bookingReference, status);
    basketOutPort.changeBasketStatus(bookingReference, status);
    log.info("Basket status changed successfully [bookingReference={}, status={}]",
        bookingReference, status);
  }
}
