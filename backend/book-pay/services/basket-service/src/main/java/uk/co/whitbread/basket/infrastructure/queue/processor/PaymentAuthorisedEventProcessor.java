package uk.co.whitbread.basket.infrastructure.queue.processor;

import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMENDING;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PAY_PENDING;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PROCESSING;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.exception.PaymentFraudException;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.Country;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.model.payments.out.OrchestrationPaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BookingConfirmationDetailsConverterPort;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.infrastructure.queue.model.PaymentAuthorisedEvent;
import uk.co.whitbread.basket.infrastructure.queue.producer.BasketOrderProducer;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentAuthorisedEventProcessor {

  private final BasketOrderProducer basketOrderProducer;
  private final BasketOutPort basketOutPort;
  private final PaymentAuthorisedEventPaymentResponseMapper paymentAuthorisedEventPaymentResponseMapper;
  private final BookingConfirmationDetailsConverterPort bookingConfirmationDetailsConverterPort;
  private final ContentOutPort contentOutPort;

  public void process(final PaymentAuthorisedEvent event) {
    validateMandatoryFields(event);
    log.info("Processing PaymentAuthorisedEvent for basketReference={}", event.getBasketReference());

    Basket basket = basketOutPort.getBasketById(event.getBasketReference());
    PaymentResponse paymentResponse = paymentAuthorisedEventPaymentResponseMapper
        .toPaymentsResponseModel(event.getPaymentsConfirmation(), basket);
    paymentResponse.setBookingReference(basket.getReference());
    //TODO need to delete after is for DEMO
    basket.setPaymentOption(event.getPaymentsConfirmation().getPaymentOptionSelected());

    if (PROCESSING.equals(basket.getStatus()) || AMENDING.equals(basket.getStatus())) {
      log.warn(
          "Skipping reprocessing of PaymentAuthorisedEvent for basketReference={} "
              + "as basket is already in status={}. "
              + "This is likely a retry of a previously processed event.",
          event.getBasketReference(), basket.getStatus());
      return;
    }

    if (PAY_PENDING.equals(basket.getStatus())) {
      validatePaymentResponseStatus(event.getBasketReference(), paymentResponse);

      if (StringUtils.isNotBlank(basket.getOriginalBasketId())) {
        var paymentsConfirmation = event.getPaymentsConfirmation();
        processAmend(basket, paymentsConfirmation);
      } else {
        var confirmationPaymentDetails = getConfirmationPaymentDetails(basket, paymentResponse,
            event.getPaymentProvider());
        Optional.ofNullable(event.getPaymentProvider())
            .map(String::toUpperCase)
            .ifPresent(basket::setPaymentProvider);
        basketOutPort.updateBasketPayment(basket);
        String ccAgentId = Optional.ofNullable(event.getPaymentsConfirmation().getCcAgentId()).orElse("");
        processBasketOrder(basket, confirmationPaymentDetails, ccAgentId);
      }
    } else {
      log.error(
          "Unexpected basket status={} for basketReference={}. Expected PAY_PENDING. "
              + "Event will not be processed.",
          basket.getStatus(), event.getBasketReference());
    }
  }

  private void validateMandatoryFields(final PaymentAuthorisedEvent event) {
    if (event == null) {
      throw new IllegalArgumentException("PaymentAuthorisedEvent is required");
    }
    if (StringUtils.isBlank(event.getBasketReference())) {
      throw new IllegalArgumentException("PaymentAuthorisedEvent basketReference is required");
    }
    if (event.getPaymentsConfirmation() == null) {
      throw new IllegalArgumentException("PaymentAuthorisedEvent paymentsConfirmation is required");
    }
    if (StringUtils.isBlank(event.getPaymentsConfirmation().getPaymentId())) {
      throw new IllegalArgumentException("PaymentAuthorisedEvent paymentsConfirmation.paymentId is required");
    }
  }

  private void processBasketOrder(
      final Basket basket, final BookingConfirmationDetails bookingConfirmationDetails, String ccAgentId) {
    basketOrderProducer.sendOrderAsync(
        basket,
        BasketRequestAction.COMMIT.getReqAction(),
        bookingConfirmationDetails,
        ccAgentId);
    
    try {
      basketOutPort.updateBasketStatus(basket.getBasketId(), PROCESSING, Optional.empty());
    } catch (Exception e) {
      log.error(
          "Failed to update basket status to PROCESSING after sending order. "
              + "Basket id={}, reference={}. Order was already sent to broker. "
              + "Manual intervention required to reconcile basket state.",
          basket.getBasketId(), basket.getReference(), e);
    }
  }

  private void processAmend(Basket basket, PaymentsConfirmation paymentsConfirmation) {
    basketOrderProducer
        .sendAmendAsync(basket, paymentsConfirmation, BasketRequestAction.AMEND.getReqAction());
    basketOutPort.updateBasketStatus(basket.getBasketId(), BasketStatus.AMENDING, Optional.empty());
  }

  private BookingConfirmationDetails getConfirmationPaymentDetails(Basket basket,
      PaymentResponse paymentResponse, String paymentProvider) {
    HotelPaymentInformation hotelPaymentInformation =
        contentOutPort.getHotelPaymentDetails(basket.getHotelId(),
            Country.valueOf(paymentResponse.getBooking().getLanguage().toUpperCase())
                .getCountryCode(),
            paymentResponse.getBooking().getLanguage().toLowerCase());
    return bookingConfirmationDetailsConverterPort.createConfirmationDetails(basket,
        hotelPaymentInformation, paymentResponse, paymentProvider);
  }

  private void validatePaymentResponseStatus(String basketReference, PaymentResponse paymentResponse) {
    switch (OrchestrationPaymentStatus.valueOf(paymentResponse.getPaymentStatus())) {
      case FAILURE -> {
        var fraudDecision = Optional.ofNullable(
                paymentResponse.getProviderResponse().getThreecResponse().getFraudCheckDecision())
            .orElse("");
        if (!fraudDecision.equals("ACCEPT")) {
          var message = String.format(
              "A fraud check was triggered and no payment has been made. Payment status from orchestration is %s",
              paymentResponse.getPaymentStatus());
          var exception = new PaymentFraudException(ErrorCode.DIGITAL_FAILURE_NOT_ACCEPT_EXCEPTION,
              message);
          ExceptionLogger.log(log, exception);
          throw exception;
        } else {
          var message = String.format(
              "A failure has occurred from orchestration for paymentID %s, provider reason: %s",
              paymentResponse.getPaymentId(),
              paymentResponse.getProviderResponse().getThreecResponse().getProviderReason());
          var exception = new PaymentException(ErrorCode.DIGITAL_FAILURE_DATATRANS_EXCEPTION, message);
          ExceptionLogger.log(log, exception);
          throw exception;
        }
      }
      case AUTHORIZED ->
        log.info("Successful provider result, settlement still in pending for paymentID {}",
            paymentResponse.getPaymentId());

      case SUCCESS -> log.info("Success payment with paymentID {}", paymentResponse.getPaymentId());
      default -> {
        var message = String.format(
            "A failure has occurred from orchestration for paymentID %s", paymentResponse.getPaymentId());
        var exception = new PaymentException(ErrorCode.DIGITAL_PAYMENT_FAILURE_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
  }

  private void validateBasketReference(PaymentResponse paymentResponse, String basketReference) {
    if (!paymentResponse.getBooking().getReference().equals(basketReference)) {
      var exception = new BasketReferenceNotValidException(
          ErrorCode.DIGITAL_INVALID_BASKET_REF_EXCEPTION,
          "The basket reference is not the same as the booking reference!");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }
}
