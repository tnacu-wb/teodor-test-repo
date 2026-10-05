package uk.co.whitbread.basket.infrastructure.rest.client.payments.threec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.LocalDate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.exception.PaymentInvalidException;
import uk.co.whitbread.basket.domain.exception.PaymentProcessingException;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.generated.models.payments.AddressDto;
import uk.co.whitbread.basket.generated.models.payments.AmountDto;
import uk.co.whitbread.basket.generated.models.payments.BillingDto;
import uk.co.whitbread.basket.generated.models.payments.BookingDto;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.JourneyEnum;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.LanguageEnum;
import uk.co.whitbread.basket.generated.models.payments.BusinessSiteDto;
import uk.co.whitbread.basket.generated.models.payments.CardDto;
import uk.co.whitbread.basket.generated.models.payments.PaymentDto;
import uk.co.whitbread.basket.generated.models.payments.PaymentDto.SubTypeEnum;
import uk.co.whitbread.basket.generated.models.payments.PaymentDto.TypeEnum;
import uk.co.whitbread.basket.generated.models.payments.PaymentRequestDto;
import uk.co.whitbread.basket.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.basket.generated.models.payments.ProviderResponseDto;
import uk.co.whitbread.basket.generated.models.payments.RefundDto;
import uk.co.whitbread.basket.generated.models.payments.ThreeCResponseDto;
import uk.co.whitbread.basket.generated.models.payments.TokenRefundRequestDto;
import uk.co.whitbread.basket.generated.models.payments.TokenRefundResponseDto;
import uk.co.whitbread.basket.generated.models.payments.UpdateTokenRequestDto;
import uk.co.whitbread.basket.generated.models.payments.UpdateTokenResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponsePaypal0Spec;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponsePaypal1Spec;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponsePaypal2Spec;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponseSpec;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.properties.ThreecProperties;

@ExtendWith(MockitoExtension.class)
class PaymentsClientTest {

  private static final String EXPECTED_MESSAGE = "An error was returned calling the Payment service:";
  private static final String EXPECTED_MESSAGE_PAYPAL_CREATE_CUSTOMER_FAIL = "paypal customer creation is unsuccessful: Unknown or expired payment_method_nonce.: 61781766425D";
  private static final String EXPECTED_MESSAGE_PAYPAL_TIMEOUT_FAIL = "Unable to handle create paypal payment request due to timed out with payment id 41814323415D";
  private static final String EXPECTED_MESSAGE_PAYPAL_REFUSED_FAIL = "PayPal Forwarding API Transaction Declined/Refused: FAILED";
  private static final String MESSAGE = "Improper call of Payment Service";
  private static final UpdateTokenRequestDto UPDATE_TOKEN_REQUEST_DTO = new UpdateTokenRequestDto();
  @InjectMocks
  private PaymentsClient paymentsClient;
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private ExchangeFunction exchangeFunction;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @Mock
  private CustomTestResponsePaypal0Spec responseSpecPaypal0Mock;
  @Mock
  private CustomTestResponsePaypal1Spec responseSpecPaypal1Mock;
  @Mock
  private CustomTestResponsePaypal2Spec responseSpecPaypal2Mock;
  @Mock
  private ThreecProperties threecProperties;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private UriBuilder uriBuilder;

  @Test
  void getPaymentConfirmation__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PaymentResponseDto.class)).thenReturn(
        mockPaymentResponse());

    //Act
    var paymentResponse = this.paymentsClient.getPaymentConfirmation("1234");

    //Assert
    assertThat(paymentResponse, notNullValue());
  }

  @Test
  void getPaymentConfirmation_4xx_exception() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(PaymentInvalidException.class,
        () -> paymentsClient.getPaymentConfirmation("1234"));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(MESSAGE));
  }

  @Test
  void getPaymentConfirmation_5xx_exception() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(PaymentException.class,
        () -> paymentsClient.getPaymentConfirmation("1234"));

    //Assert
    String expectedMessage = "An error occurred calling the 3CP service to get the payment confirmation";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void createPaymentWithMandatoryParams__ShouldReturnOK() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PaymentResponseDto.class))
        .thenReturn(mockPaymentResponse());

    //Act
    var paymentResponse = paymentsClient.createPayment(getPaymentRequest());

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getPaymentId(), is("04230602742D"));
    assertThat(paymentResponse.getProviderResponse().getThreecResponse().getTemplate(),
        is("wb_newcard_pn_v3.xml"));
    assertThat(paymentResponse.getProviderResponse().getThreecResponse().getiPageHtml(),
        is("PCFET0NUWVBFIGh0bWw+CjxodG1sPgo8Ym9keSBzdHlsZT0icG9zaXRpb246IGFic29sdXRlOyB0b3A6IDUwJTsgdHJhbnNmb3JtOiB0cmFu"));
  }

  @Test
  void createMitCcPayment_returnsPaymentResponse_whenRequestIsValid() {
    //Arrange
    when(threecProperties.getPaymentsEndpoint()).thenReturn("/payments");
    when(uriBuilder.path("/payments")).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(URI.create("/payments"));
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
      uriFunction.apply(uriBuilder);
      return requestBodySpec;
    });
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PaymentResponseDto.class)).thenReturn(mockPaymentResponse());

    //Act
    var paymentResponse = paymentsClient.createMitCcPayment(getPaymentRequest());

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getPaymentId(), is("04230602742D"));
    verify(uriBuilder).path("/payments");
    verify(uriBuilder).build();
  }

  @Test
  void createMitCcPayment_throwsPaymentProcessingException_whenPaymentsServiceReturnsError() {
    //Arrange
    when(threecProperties.getPaymentsEndpoint()).thenReturn("/payments");
    when(uriBuilder.path("/payments")).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(URI.create("/payments"));
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
      uriFunction.apply(uriBuilder);
      return requestBodySpec;
    });
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PaymentResponseDto.class)).thenReturn(
        Mono.error(new PaymentProcessingException("m", "d", new Throwable(), 100)));

    var paymentRequest = getPaymentRequest();

    //Act
    var exception = assertThrows(PaymentProcessingException.class,
        () -> paymentsClient.createMitCcPayment(paymentRequest));

    //Assert
    assertThat(exception, notNullValue());
    verify(uriBuilder).path("/payments");
    verify(uriBuilder).build();
  }


  @Test
  void createPayment_4xx_exception() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    var paymentRequest = getPaymentRequest();

    //Act
    Exception exception = assertThrows(PaymentInvalidException.class,
        () -> paymentsClient.createPayment(paymentRequest));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(MESSAGE));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void createPayment_5xx_exception(boolean mockedFeaturePaypalErrorMappingFf) {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeaturePaypalErrorMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getPaypalErrorMapping()).thenReturn(mockedFeaturePaypalErrorMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPaypalErrorMapping())).thenReturn(
        mockedFeaturePaypalErrorMappingFf);
    var paymentRequest = getPaymentRequest();

    //Act
    Exception exception = assertThrows(PaymentException.class,
        () -> paymentsClient.createPayment(paymentRequest));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
  }

  @Test
  void createPayment_5xx_paypal_create_customer_exception() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecPaypal0Mock);
    when(responseSpecPaypal0Mock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecPaypal0Mock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeaturePaypalErrorMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getPaypalErrorMapping()).thenReturn(mockedFeaturePaypalErrorMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPaypalErrorMapping())).thenReturn(true);
    var paymentRequest = getPaypalPaymentRequest();

    //Act
    Exception exception = assertThrows(PaymentException.class,
        () -> paymentsClient.createPayment(paymentRequest));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXPECTED_MESSAGE_PAYPAL_CREATE_CUSTOMER_FAIL));
  }

  @Test
  void createPayment_5xx_paypal_timeout_exception() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecPaypal1Mock);
    when(responseSpecPaypal1Mock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecPaypal1Mock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeaturePaypalErrorMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getPaypalErrorMapping()).thenReturn(mockedFeaturePaypalErrorMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPaypalErrorMapping())).thenReturn(true);
    var paymentRequest = getPaypalPaymentRequest();

    //Act
    Exception exception = assertThrows(PaymentException.class,
        () -> paymentsClient.createPayment(paymentRequest));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXPECTED_MESSAGE_PAYPAL_TIMEOUT_FAIL));
  }

  @Test
  void createPayment_5xx_paypal_refused_exception() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecPaypal2Mock);
    when(responseSpecPaypal2Mock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecPaypal2Mock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeaturePaypalErrorMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getPaypalErrorMapping()).thenReturn(mockedFeaturePaypalErrorMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPaypalErrorMapping())).thenReturn(true);
    var paymentRequest = getPaypalPaymentRequest();

    //Act
    Exception exception = assertThrows(PaymentException.class,
        () -> paymentsClient.createPayment(paymentRequest));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXPECTED_MESSAGE_PAYPAL_REFUSED_FAIL));
  }

  @Test
  void updateToken__ShouldReturnOk() {

    //Arrange
    when(threecProperties.getTokensEndpoint()).thenReturn("Some token");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("Some token")).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(UpdateTokenResponseDto.class))
        .thenReturn(mockUpdateTokenResponse());

    // Act
    var updateTokenResponse = paymentsClient.updateToken(UPDATE_TOKEN_REQUEST_DTO);

    //Assert
    assertThat(updateTokenResponse, notNullValue());
  }

  private Mono<UpdateTokenResponseDto> mockUpdateTokenResponse() {

    var updateTokenResponse = new UpdateTokenResponseDto();
    updateTokenResponse.setToken("someToken");
    return Mono.just(updateTokenResponse);

  }


  @Test
  void createTokenRefundRequest__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(TokenRefundResponseDto.class)).thenReturn(mockRefundResponse());

    //Act
    var refundResponse = paymentsClient.sendPartialRefund(getTokenRefundRequest());

    //Assert
    assertThat(refundResponse, notNullValue());
    assertThat(refundResponse.getRefundId(), is("123refund"));
    assertThat(refundResponse.getPaymentId(), is("123paymentId"));
    assertThat(refundResponse.getRequestId(), is("123reqId"));
    assertTrue(refundResponse.getRefunded());
  }

  @Test
  void sendTokenRefund_4xx_exception() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    var tokenRefundRequest = getTokenRefundRequest();

    //Act
    Exception exception = assertThrows(PaymentInvalidException.class,
        () -> paymentsClient.sendPartialRefund(tokenRefundRequest));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(MESSAGE));
  }

  @Test
  void sendTokenRefund_5xx_exception() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    var tokenRefundRequest = getTokenRefundRequest();

    //Act
    Exception exception = assertThrows(PaymentException.class,
        () -> paymentsClient.sendPartialRefund(tokenRefundRequest));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
  }

  @Test
  void updateTokenAndFeatureFlagTrue5xx__ShouldReturnException() {
    //Arrange
    when(threecProperties.getTokensEndpoint()).thenReturn("Some token");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("Some token")).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    assertThrows(PaymentException.class, () ->
        paymentsClient.updateToken(UPDATE_TOKEN_REQUEST_DTO));
    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateTokenAndFeatureFlagTrue4xx__ShouldReturnException() {
    //Arrange
    when(threecProperties.getTokensEndpoint()).thenReturn("Some token");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("Some token")).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    assertThrows(PaymentInvalidException.class, () ->
        paymentsClient.updateToken(UPDATE_TOKEN_REQUEST_DTO));
    //Assert
    verifyNoMoreInteractions(webClient);
  }


  private ThreeCResponseDto mockThreecResponse() {
    ThreeCResponseDto threeCResponseDto = new ThreeCResponseDto();
    threeCResponseDto.setTemplate("wb_newcard_pn_v3.xml");
    threeCResponseDto.setiPageHtml(
        "PCFET0NUWVBFIGh0bWw+CjxodG1sPgo8Ym9keSBzdHlsZT0icG9zaXRpb246IGFic29sdXRlOyB0b3A6IDUwJTsgdHJhbnNmb3JtOiB0cmFu");
    return threeCResponseDto;
  }

  private Mono<PaymentResponseDto> mockPaymentResponse() {
    PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
    ProviderResponseDto providerResponse = new ProviderResponseDto();
    providerResponse.setThreecResponse(mockThreecResponse());
    paymentResponseDto.setProviderResponse(providerResponse);
    paymentResponseDto.setPaymentId("04230602742D");
    return Mono.just(paymentResponseDto);
  }

  private PaymentDto getPayment(SubTypeEnum subType) {

    var payment = new PaymentDto();
    var billing = new BillingDto();
    var address = new AddressDto();
    var card = new CardDto();

    payment.setBilling(billing);
    payment.setEnvironment("https://www.premierinn.com");
    payment.setSubType(subType);
    payment.setType(TypeEnum.CARD);
    billing.setAddress(address);

    billing.setEmail("test@whitbread.com");
    billing.setFirstName("Smith");
    billing.setLastName("Will");
    billing.setTitle("Mr");

    address.setCountryCode("GB");
    address.setLine1("ADDRESS LINE 1");

    card.setExpiryMonth("01");
    card.setExpiryYear("23");
    card.setToken("4764776852337921111");

    return payment;

  }

  private TokenRefundRequestDto getTokenRefundRequest() {
    var tokenRefundRequestDto = new TokenRefundRequestDto();

    var amountDto = new AmountDto();
    amountDto.currency(AmountDto.CurrencyEnum.GBP);
    amountDto.minorUnits(10);

    var cardDto = new CardDto();
    cardDto.token("4216333880397891103");
    cardDto.expiryYear("26");
    cardDto.expiryMonth("03");

    var refundDto = new RefundDto();
    refundDto.amount(amountDto);
    refundDto.card(cardDto);
    refundDto.type(RefundDto.TypeEnum.CARD);
    refundDto.reason("Amend");

    var businessSite = new BusinessSiteDto();
    businessSite.identifier("LONEUS");
    businessSite.type(BusinessSiteDto.TypeEnum.HOTEL);

    var bookingDto = new BookingDto();
    bookingDto.channel(BookingDto.ChannelEnum.WEB);
    bookingDto.type(BookingDto.TypeEnum.PAY_NOW);
    bookingDto.journey(JourneyEnum.REFUND);

    tokenRefundRequestDto.requestId("ABCD#12345");
    tokenRefundRequestDto.hotelCode("LONEUS");
    tokenRefundRequestDto.refund(refundDto);
    tokenRefundRequestDto.booking(bookingDto);

    return tokenRefundRequestDto;
  }

  private Mono<TokenRefundResponseDto> mockRefundResponse() {
    var refundResponseDto = new TokenRefundResponseDto();
    refundResponseDto.refundId("123refund");
    refundResponseDto.setRefunded(true);
    refundResponseDto.setPaymentId("123paymentId");
    refundResponseDto.setRequestId("123reqId");
    return Mono.just(refundResponseDto);
  }

  private BookingDto getBooking() {

    var booking = new BookingDto();
    var businessSite = new BusinessSiteDto();

    businessSite.setIdentifier("DUNGOU");
    businessSite.setType(BusinessSiteDto.TypeEnum.HOTEL);
    businessSite.setLocation("Dundee");
    businessSite.setName("Dundee West");

    booking.setArrivalDate(LocalDate.parse("2022-10-01"));
    booking.setDepartureDate(LocalDate.parse("2022-10-02"));

    booking.setLanguage(LanguageEnum.EN);
    booking.setType(BookingDto.TypeEnum.PAY_NOW);
    booking.setJourney(JourneyEnum.BOOKING);
    booking.setBusinessSite(businessSite);

    return booking;

  }

  private PaymentRequestDto getPaymentRequest() {

    var request = new PaymentRequestDto();

    request.setRequestId("123");
    request.setBooking(getBooking());
    request.setPayment(getPayment(SubTypeEnum.ECOMM));

    return request;
  }

  private PaymentRequestDto getPaypalPaymentRequest() {

    var request = new PaymentRequestDto();

    request.setRequestId("123");
    request.setBooking(getBooking());
    request.setPayment(getPayment(SubTypeEnum.MIT));

    return request;
  }

}