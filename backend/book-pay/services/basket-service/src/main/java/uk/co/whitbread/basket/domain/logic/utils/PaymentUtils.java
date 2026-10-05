package uk.co.whitbread.basket.domain.logic.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.CardDetailsDeclinedException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.exception.PaymentFraudException;
import uk.co.whitbread.basket.domain.exception.UnSupportedCardTypeException;
import uk.co.whitbread.basket.domain.model.content.out.AcceptedCreditCard;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.model.payments.out.CardData;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreecPaymentStatus;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentUtils {

  public static final String PIBA_UK_CARD_TYPE = "BU";
  public static final String PIBA_EURO_CARD_TYPE = "BD";

  public static void validatePaymentResponse(String basketReference,
      PaymentResponse paymentResponse) {
    validateBasketReference(paymentResponse, basketReference);
    switch (ThreecPaymentStatus.valueOf(paymentResponse.getPaymentStatus())) {
      case NO_PAYMENT_ATTEMPT -> {
        var message = String.format(
            "Card was declined and no payment attempt has been made. Payment status from threec is %s",
            paymentResponse.getPaymentStatus());
        var exception = new CardDetailsDeclinedException(
            ErrorCode.DIGITAL_NO_PAYMENT_ATTEMPT_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
      case FAILURE -> {
        var fraudDecision = Optional.ofNullable(
                paymentResponse.getProviderResponse().getThreecResponse().getFraudCheckDecision())
            .orElse("");
        if (!fraudDecision.equals("ACCEPT")) {
          var message = String.format(
              "A fraud check was triggered and no payment has been made. Payment status from threec is %s",
              paymentResponse.getPaymentStatus());
          var exception = new PaymentFraudException(ErrorCode.DIGITAL_FAILURE_NOT_ACCEPT_EXCEPTION,
              message);
          ExceptionLogger.log(log, exception);
          throw exception;
        } else {
          var message = String.format(
              "A failure has occurred from 3CP for paymentID %s, provider reason: %s",
              paymentResponse.getPaymentId(),
              paymentResponse.getProviderResponse().getThreecResponse().getProviderReason());
          var exception = new PaymentException(ErrorCode.DIGITAL_FAILURE_3CP_EXCEPTION, message);
          ExceptionLogger.log(log, exception);
          throw exception;
        }
      }
      case PENDING -> {
        var message = "Unsuccessful provider result, payment still in pending";
        var exception = new PaymentException(ErrorCode.DIGITAL_UNSUCCESSFULL_PROVIDER_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
      case SUCCESS -> log.info("Success payment with paymentID {}", paymentResponse.getPaymentId());
      default -> {
        var message = String.format(
            "A failure has occurred from 3CP for paymentID %s", paymentResponse.getPaymentId());
        var exception = new PaymentException(ErrorCode.DIGITAL_PAYMENT_FAILURE_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
  }

  public static BookingConfirmationDetails createConfirmationPaymentDetails(String bookingChannel,
      HotelPaymentInformation hotelPaymentInformation, PaymentResponse paymentResponse,
      UnleashWrapper<FeatureFlag> unleashWrapper, String paymentProvider) {
    BookingConfirmationDetails bookingConfirmationDetails = new BookingConfirmationDetails();
    var cardSchemeId = paymentResponse.getProviderResponse().getThreecResponse().getCardSchemeId();
    var acceptedCard = hotelPaymentInformation.getAcceptedCreditCards().stream()
        .filter(card -> "DATATRANS".equalsIgnoreCase(paymentProvider)
            ? card.getCode().equals(cardSchemeId)
            : card.getCode3CP().equals(cardSchemeId))
        .findFirst().orElse(null);
    if (acceptedCard == null) {
      var message = String.format("Unsupported cardType=%s",
          paymentResponse.getProviderResponse().getThreecResponse().getCardSchemeId());
      var exception = new PaymentException(ErrorCode.DIGITAL_UNSUPPORTED_CARD_TYPE_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    } else {
      if (paymentResponse != null) {
        bookingConfirmationDetails.setCardData(CardData.builder().token(paymentResponse.getProviderResponse()
                .getThreecResponse().getToken()).cardHolderName(cardHolderName(paymentResponse))
            .last4Digits(paymentResponse.getProviderResponse()
                .getThreecResponse().getLast4Digits())
            .expirationDate(expirationDate(paymentResponse.getProviderResponse().getThreecResponse().getExpiry()))
            .build());
      }

      bookingConfirmationDetails.setPaymentType(acceptedCard.getCodeOpera());
      var operaPaymentMethod = hotelPaymentInformation.getPaymentMethodsOpera();
      bookingConfirmationDetails.setPaymentMethod(
          operaPaymentMethod != null ? operaPaymentMethod.get(
              bookingChannel.concat("_")
                  .concat(bookingConfirmationDetails.getPaymentType())) : null);
      bookingConfirmationDetails.setCardType(
          getCardType(unleashWrapper, bookingConfirmationDetails.getPaymentType(), acceptedCard));
    }

    return bookingConfirmationDetails;
  }

  public static BookingConfirmationDetails createDistributionConfirmationPaymentDetails(String bookingChannel,
      HotelPaymentInformation hotelPaymentInformation, PaymentRequest paymentRequest,
      UnleashWrapper<FeatureFlag> unleashWrapper) {
    BookingConfirmationDetails bookingConfirmationDetails = new BookingConfirmationDetails();
    var acceptedCard = hotelPaymentInformation.getAcceptedCreditCards().stream()
        .filter(card -> card.getCode3CP()
            .equals(paymentRequest.getPayment().getCard().getCardType()))
        .findFirst().orElse(null);
    if (acceptedCard == null) {
      var message = String.format("Unsupported cardType=%s",
          paymentRequest.getPayment().getCard().getCardType());
      var exception = new UnSupportedCardTypeException(
          ErrorCode.DIGITAL_AC_NULL_BOOKING_CHANNEL_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    } else {
      bookingConfirmationDetails.setCardData(CardData.builder().token(paymentRequest.getPayment().getCard().getToken())
          .cardHolderName(paymentRequest.getPayment().getCard().getCardholderName())
          .last4Digits(paymentRequest.getPayment().getCard().getLast4Digits()).expirationDate(expirationDate(
              paymentRequest.getPayment().getCard().getExpiryMonth().concat("/")
                  .concat(paymentRequest.getPayment().getCard().getExpiryYear()))).build());
      bookingConfirmationDetails.setPaymentType(acceptedCard.getCodeOpera());
      if (paymentRequest.getBooking().getChannel() == null) {
        var exception = new PaymentException(
            ErrorCode.DIGITAL_PAYMENT_FAILURE_EXCEPTION, "Booking channel not present");
        ExceptionLogger.log(log, exception);
        throw exception;
      }
      var operaPaymentMethod = hotelPaymentInformation.getPaymentMethodsOpera();
      bookingConfirmationDetails.setPaymentMethod(
          operaPaymentMethod != null ? operaPaymentMethod.get(
              bookingChannel.concat("_")
                  .concat(bookingConfirmationDetails.getPaymentType())) : null);

      bookingConfirmationDetails.setCardType(
          getCardType(unleashWrapper, bookingConfirmationDetails.getPaymentType(), acceptedCard));
    }

    return bookingConfirmationDetails;
  }

  private static String getCardType(UnleashWrapper<FeatureFlag> unleashWrapper,
      String paymentType, AcceptedCreditCard acceptedCard) {

    return isPiba(paymentType) && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getPibaBooking())
        ? acceptedCard.getCodeOpera()
        : acceptedCard.getCodeOperaCardType();
  }

  private static boolean isPiba(String paymentType) {
    return PIBA_UK_CARD_TYPE.equals(paymentType) || PIBA_EURO_CARD_TYPE.equals(paymentType);
  }

  private static void validateBasketReference(PaymentResponse paymentResponse,
      String basketReference) {
    if (!paymentResponse.getBooking().getReference().equals(basketReference)) {
      var exception = new BasketReferenceNotValidException(
          ErrorCode.DIGITAL_INVALID_BASKET_REF_EXCEPTION,
          "The basket reference is not the same as the booking reference!");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private static String cardHolderName(PaymentResponse paymentResponse) {
    if (paymentResponse == null 
        || paymentResponse.getPayment() == null 
        || paymentResponse.getPayment().getBilling() == null
        || paymentResponse.getPayment().getBilling().getFirstName() == null
        || paymentResponse.getPayment().getBilling().getLastName() == null) {
      return null;
    }
    var decoder = Base64.getDecoder();
    try {
      byte[] lastName = decoder.decode(paymentResponse.getPayment().getBilling().getLastName());
      byte[] firstName = decoder.decode(paymentResponse.getPayment().getBilling().getFirstName());
      return new String(firstName, UTF_8) + " " + new String(lastName, UTF_8);
    } catch (IllegalArgumentException e) {
      log.warn("Failed to decode cardholder name, returning null", e);
      return null;
    }
  }

  private static String expirationDate(final String expirationMonthYearFormat) {
    var expirationDate = YearMonth.parse(expirationMonthYearFormat,
        DateTimeFormatter.ofPattern("MM/yy"));
    return expirationDate.atEndOfMonth().toString();
  }
}
