package uk.co.whitbread.basket.domain.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.CardDetailsDeclinedException;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.exception.PaymentFraudException;
import uk.co.whitbread.basket.domain.exception.UnSupportedCardTypeException;
import uk.co.whitbread.basket.domain.logic.utils.PaymentUtils;
import uk.co.whitbread.basket.domain.model.content.out.AcceptedCreditCard;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.Billing;
import uk.co.whitbread.basket.domain.model.payments.out.Payment;
import uk.co.whitbread.basket.domain.model.payments.out.Booking;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ProviderResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreeCResponse;


@ExtendWith(SpringExtension.class)
class PaymentUtilsTest {

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @BeforeEach
  void setUp() {
    unleashWrapper = Mockito.mock(UnleashWrapper.class);
  }

  @Test
  void createConfirmationPaymentDetails_UnsupportedCardType() {
    //Arrange
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder()
            .billing(Billing.builder()
                .firstName("34tq34y45y45y45y")
                .lastName("lastbbbbbN45y45ame")
                .build())
            .build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("code3CP")
                .expiry("10/24")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("code3CP")
            .build()))
        .build();

    //Act
    assertDoesNotThrow(
        () -> PaymentUtils.createConfirmationPaymentDetails("channel",
            hotelPaymentInformation,
            paymentResponse,
            unleashWrapper, null));
  }

  @ParameterizedTest
  @CsvSource({
      "BU, BU, true",
      "BD, BD, true",
      "BU, Zz, false",
      "BD, Zz, false"
  })
  void createConfirmationPaymentDetails_PibaCardType(String codeOpera, String expectedCardType,
      boolean featureFlagValue) {
    //Arrange
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder()
            .billing(Billing.builder()
                .firstName("34tq34y45y45y45y")
                .lastName("lastbbbbbN45y45ame")
                .build())
            .build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("code3CP")
                .expiry("10/24")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("code3CP")
            .codeOpera(codeOpera)
            .codeOperaCardType("Zz")
            .build()))
        .build();

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getPibaBooking()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPibaBooking())).thenReturn(featureFlagValue);

    //Act
    var bookingConfirmationDetails = PaymentUtils.createConfirmationPaymentDetails(
        "channel", hotelPaymentInformation, paymentResponse, unleashWrapper, null);

    // Assert
    assertNotNull(bookingConfirmationDetails);
    assertThat(expectedCardType, is(bookingConfirmationDetails.getCardType()));
  }

  @Test
  void createDistributionConfirmationPaymentDetails_UnSupportedCardTypeException() {
    //Arrange
    uk.co.whitbread.basket.domain.model.payments.in.Card card = uk.co.whitbread.basket.domain.model.payments.in.Card.builder()
        .cardType("code3CP")
        .type("type")
        .expiryMonth("11")
        .expiryYear("34")
        .token("token")
        .build();

    uk.co.whitbread.basket.domain.model.payments.in.Payment payment = uk.co.whitbread.basket.domain.model.payments.in.Payment.builder()
        .card(card)
        .type("a")
        .subType("ab")
        .build();
    uk.co.whitbread.basket.domain.model.payments.in.Booking booking = uk.co.whitbread.basket.domain.model.payments.in.Booking.builder()
        .channel("channel")
        .journey("journey")
        .type("type")
        .businessSite(BusinessSite.builder().identifier("d").type("type").build())
        .build();
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .booking(uk.co.whitbread.basket.domain.model.payments.in.Booking.builder()
            .type("type")
            .journey("jouney")
            .businessSite(BusinessSite.builder().identifier("d").type("type").build())
            .channel("channel")
            .build())
        .payment(payment)
        .booking(booking)
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("code3CP")
            .build()))
        .build();

    assertDoesNotThrow(
        () -> PaymentUtils.createDistributionConfirmationPaymentDetails("channel",
            hotelPaymentInformation, paymentRequest, unleashWrapper));

  }

  @Test
  void createDistributionConfirmationPaymentDetails_UnsupportedCardType() {
    //Arrange
    uk.co.whitbread.basket.domain.model.payments.in.Card card = uk.co.whitbread.basket.domain.model.payments.in.Card.builder()
        .type("type")
        .build();
    uk.co.whitbread.basket.domain.model.payments.in.Payment payment = uk.co.whitbread.basket.domain.model.payments.in.Payment.builder()
        .card(card).type("a")
        .subType("ab")
        .type("type")
        .build();
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .booking(uk.co.whitbread.basket.domain.model.payments.in.Booking.builder()
            .channel("channel")
            .journey("journey")
            .type("type")
            .businessSite(BusinessSite.builder().identifier("d").type("type").build()).
            build())
        .payment(payment)
        .build();

    String bookingChannel = "bookingChannel";

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("code3CP")
            .build()))
        .build();

    //Act
    assertThrows(UnSupportedCardTypeException.class,
        () -> PaymentUtils.createDistributionConfirmationPaymentDetails(bookingChannel,
            hotelPaymentInformation, paymentRequest, unleashWrapper));
  }

  @Test
  void validatePaymentResponse_BasketReferenceNotValidException() {
    //Arrange
    String basketReference = "basketReference";
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .booking(Booking.builder().reference("ref").build())
        .build();

    //Act
    assertThrows(BasketReferenceNotValidException.class,
        () -> PaymentUtils.validatePaymentResponse(basketReference, paymentResponse));
  }

  @Test
  void validatePaymentResponse_NO_PAYMENT_ATTEMPT_CardDetailsDeclinedException() {
    //Arrange
    String basketReference = "basketReference";
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentStatus("NO_PAYMENT_ATTEMPT")
        .paymentId("paymentId")
        .booking(Booking.builder().reference("basketReference").build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .fraudCheckDecision("ACCEPT")
                .build())
            .build())
        .bookingReference(basketReference)
        .build();

    //Act
    assertThrows(CardDetailsDeclinedException.class,
        () -> PaymentUtils.validatePaymentResponse(basketReference, paymentResponse));
  }

  @Test
  void validatePaymentResponse_FAILURE_PaymentFraudException() {
    //Arrange
    String basketReference = "basketReference";
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentStatus("FAILURE")
        .booking(Booking.builder().reference("basketReference").build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .build())
            .build())
        .build();

    //Act
    assertThrows(PaymentFraudException.class,
        () -> PaymentUtils.validatePaymentResponse(basketReference, paymentResponse));
  }

  @Test
  void validatePaymentResponse_FAILURE_PaymentException() {
    //Arrange
    String basketReference = "basketReference";
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentStatus("FAILURE")
        .booking(Booking.builder().reference("basketReference").build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .fraudCheckDecision("ACCEPT")
                .build())
            .build())
        .build();

    //Act
    assertThrows(PaymentException.class,
        () -> PaymentUtils.validatePaymentResponse(basketReference, paymentResponse));
  }

  @Test
  void validatePaymentResponse_PENDING_PaymentException() {
    //Arrange
    String basketReference = "basketReference";
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentStatus("PENDING")
        .booking(Booking.builder().reference("basketReference").build())
        .providerResponse(ProviderResponse.builder()
            .build())
        .build();

    //Act
    assertThrows(PaymentException.class,
        () -> PaymentUtils.validatePaymentResponse(basketReference, paymentResponse));
  }

  @Test
  void validatePaymentResponse_SUCCESS() {
    //Arrange
    String basketReference = "basketReference";
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentStatus("SUCCESS")
        .booking(Booking.builder().reference("basketReference").build())
        .providerResponse(ProviderResponse.builder()
            .build())
        .build();

    //Act
    assertDoesNotThrow(
        () -> PaymentUtils.validatePaymentResponse(basketReference, paymentResponse));
  }

  @Test
  void createConfirmationPaymentDetails_cardHolderName_returnsNull_whenPaymentIsNull() {
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(null)
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("code3CP")
                .expiry("10/24")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("code3CP")
            .build()))
        .build();

    var result = PaymentUtils.createConfirmationPaymentDetails("channel",
        hotelPaymentInformation, paymentResponse, unleashWrapper, null);

    assertNotNull(result);
    assertThat(result.getCardData().getCardHolderName(), is(org.hamcrest.Matchers.nullValue()));
  }

  @Test
  void createConfirmationPaymentDetails_cardHolderName_returnsNull_whenBillingIsNull() {
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder().billing(null).build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("code3CP")
                .expiry("10/24")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("code3CP")
            .build()))
        .build();

    var result = PaymentUtils.createConfirmationPaymentDetails("channel",
        hotelPaymentInformation, paymentResponse, unleashWrapper, null);

    assertNotNull(result);
    assertThat(result.getCardData().getCardHolderName(), is(org.hamcrest.Matchers.nullValue()));
  }

  @Test
  void createConfirmationPaymentDetails_cardHolderName_returnsNull_whenFirstNameIsNull() {
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder()
            .billing(Billing.builder().firstName(null).lastName("bGFzdA==").build())
            .build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("code3CP")
                .expiry("10/24")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("code3CP")
            .build()))
        .build();

    var result = PaymentUtils.createConfirmationPaymentDetails("channel",
        hotelPaymentInformation, paymentResponse, unleashWrapper, null);

    assertNotNull(result);
    assertThat(result.getCardData().getCardHolderName(), is(org.hamcrest.Matchers.nullValue()));
  }

  @Test
  void createConfirmationPaymentDetails_cardHolderName_returnsNull_whenLastNameIsNull() {
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder()
            .billing(Billing.builder().firstName("Zm9v").lastName(null).build())
            .build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("code3CP")
                .expiry("10/24")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("code3CP")
            .build()))
        .build();

    var result = PaymentUtils.createConfirmationPaymentDetails("channel",
        hotelPaymentInformation, paymentResponse, unleashWrapper, null);

    assertNotNull(result);
    assertThat(result.getCardData().getCardHolderName(), is(org.hamcrest.Matchers.nullValue()));
  }

  @Test
  void createConfirmationPaymentDetails_cardHolderName_returnsNull_whenBase64IsInvalid() {
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder()
            .billing(Billing.builder()
                .firstName("not-valid-base64!!!")
                .lastName("also-not-valid!!!")
                .build())
            .build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("code3CP")
                .expiry("10/24")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("code3CP")
            .build()))
        .build();

    var result = PaymentUtils.createConfirmationPaymentDetails("channel",
        hotelPaymentInformation, paymentResponse, unleashWrapper, null);

    assertNotNull(result);
    assertThat(result.getCardData().getCardHolderName(), is(org.hamcrest.Matchers.nullValue()));
  }

  @Test
  void createConfirmationPaymentDetails_datatransProvider_filtersBy_code() {
    // Arrange - card has code="VS" but code3CP="VISA_3CP"; cardSchemeId matches code
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder().build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("VS")
                .expiry("10/26")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code("VS")
            .code3CP("VISA_3CP")
            .codeOpera("VI")
            .codeOperaCardType("VISA")
            .build()))
        .build();

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getPibaBooking()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPibaBooking())).thenReturn(false);

    // Act
    var result = PaymentUtils.createConfirmationPaymentDetails("channel",
        hotelPaymentInformation, paymentResponse, unleashWrapper, "DATATRANS");

    // Assert - card matched by code, not code3CP
    assertNotNull(result);
    assertThat(result.getPaymentType(), is("VI"));
  }

  @Test
  void createConfirmationPaymentDetails_datatransProvider_unsupportedCard_throwsException() {
    // Arrange - cardSchemeId matches code3CP but not code → DATATRANS should fail to find it
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder().build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("VISA_3CP")
                .expiry("10/26")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code("VS")
            .code3CP("VISA_3CP")
            .build()))
        .build();

    // Act & Assert - DATATRANS uses code("VS") which doesn't match "VISA_3CP"
    assertThrows(PaymentException.class, () ->
        PaymentUtils.createConfirmationPaymentDetails("channel",
            hotelPaymentInformation, paymentResponse, unleashWrapper, "DATATRANS"));
  }

  @Test
  void createConfirmationPaymentDetails_threecProvider_filtersBy_code3CP() {
    // Arrange - cardSchemeId matches code3CP but not code
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder().build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("VISA_3CP")
                .expiry("10/26")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code("VS")
            .code3CP("VISA_3CP")
            .codeOpera("VI")
            .codeOperaCardType("VISA")
            .build()))
        .build();

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getPibaBooking()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPibaBooking())).thenReturn(false);

    // Act
    var result = PaymentUtils.createConfirmationPaymentDetails("channel",
        hotelPaymentInformation, paymentResponse, unleashWrapper, "THREEC");

    // Assert - card matched by code3CP
    assertNotNull(result);
    assertThat(result.getPaymentType(), is("VI"));
  }

  @Test
  void createConfirmationPaymentDetails_nullProvider_filtersBy_code3CP() {
    // Arrange - null provider defaults to code3CP filter
    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId("paymentId")
        .payment(Payment.builder().build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("VISA_3CP")
                .expiry("10/26")
                .build())
            .build())
        .booking(Booking.builder().channel("channel").build())
        .build();

    HotelPaymentInformation hotelPaymentInformation = HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code("VS")
            .code3CP("VISA_3CP")
            .codeOpera("VI")
            .codeOperaCardType("VISA")
            .build()))
        .build();

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getPibaBooking()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPibaBooking())).thenReturn(false);

    // Act
    var result = PaymentUtils.createConfirmationPaymentDetails("channel",
        hotelPaymentInformation, paymentResponse, unleashWrapper, null);

    // Assert - card matched by code3CP
    assertNotNull(result);
    assertThat(result.getPaymentType(), is("VI"));
  }

}
