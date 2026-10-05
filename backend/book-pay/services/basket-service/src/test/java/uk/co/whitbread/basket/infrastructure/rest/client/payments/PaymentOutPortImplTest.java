package uk.co.whitbread.basket.infrastructure.rest.client.payments;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.UpdateTokenRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.UpdateTokenResponse;
import uk.co.whitbread.basket.domain.model.payments.in.Address;
import uk.co.whitbread.basket.domain.model.payments.in.Billing;
import uk.co.whitbread.basket.domain.model.payments.in.Booking;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.payments.in.Card;
import uk.co.whitbread.basket.domain.model.payments.in.Payment;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.RoomType;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ProviderResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreeCResponse;
import uk.co.whitbread.basket.generated.models.payments.PaymentRequestDto;
import uk.co.whitbread.basket.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.basket.generated.models.payments.UpdateTokenRequestDto;
import uk.co.whitbread.basket.generated.models.payments.UpdateTokenResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.UpdateTokenMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.PaymentsClient;

@ExtendWith(MockitoExtension.class)
class PaymentOutPortImplTest {

  @InjectMocks
  private PaymentOutPortImpl paymentOutPort;
  @Mock
  private PaymentsClient paymentsClient;
  @Mock
  private PaymentResponseMapper paymentResponseMapper;
  @Mock
  private PaymentRequestMapper paymentRequestMapper;
  @Mock
  private UpdateTokenMapper updateTokenMapper;

  @Test
  void getPaymentConfirmation__ShouldReturnOK() {
    //Arrange
    when(paymentResponseMapper.toModel(
        any(PaymentResponseDto.class)))
        .thenReturn(mockPaymentResponse());
    when(paymentsClient.getPaymentConfirmation(anyString())).thenReturn(
        new PaymentResponseDto());

    //Act
    var paymentResponse = paymentOutPort.getPaymentConfirmation("123");

    //Assert
    assertThat(paymentResponse, notNullValue());
  }

  @Test
  void createPayment__ShouldReturnOk() {
    //Arrange
    when(paymentRequestMapper.toPaymentRequestDto(
        any(PaymentRequest.class)))
        .thenReturn(mockPaymentRequestDto());
    when(paymentsClient.createPayment(any(PaymentRequestDto.class))).thenReturn(
        mockPaymentResponseDto());
    when(paymentResponseMapper.toModel(
        any(PaymentResponseDto.class)))
        .thenReturn(mockPaymentResponse());

    //Act
    var paymentResponse = paymentOutPort.createPayment(mockPaymentRequest());

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getPaymentId(), is("04230602742D"));
    assertThat(paymentResponse.getProviderResponse().getThreecResponse().getTemplate(),
        is("wb_newcard_pn_v3.xml"));
    assertThat(paymentResponse.getProviderResponse().getThreecResponse().getIPageHtml(),
        is("PCFET0NUWVBFIGh0bWw+CjxodG1sPgo8Ym9keSBzdHlsZT0icG9zaXRpb246IGFic29sdXRlOyB0b3A6IDUwJTsgdHJhbnNmb3JtOiB0cmFu"));
  }

  @Test
  void verifyAddressParamsMandatory__ShouldReturnOk() {
    //Arrange

    //Act
    var paymentRequest = getPaymentRequest();

    //Assert
    assertThat(paymentRequest.getPayment().getBilling().getAddress().getCountryCode(),
        notNullValue());
    assertThat(paymentRequest.getPayment().getBilling().getAddress().getCountryCode(),
        is("GB"));
    assertThat(paymentRequest.getPayment().getBilling().getAddress().getLine1(), notNullValue());
    assertThat(paymentRequest.getPayment().getBilling().getAddress().getLine1(),
        is("ADDRESS LINE 1"));
    assertThat(paymentRequest.getPayment().getBilling().getAddress().getLine2(), nullValue());
    assertThat(paymentRequest.getPayment().getBilling().getAddress().getLine3(), nullValue());
    assertThat(paymentRequest.getPayment().getBilling().getAddress().getLine4(), nullValue());
    assertThat(paymentRequest.getPayment().getBilling().getAddress().getCityName(),
        is("Big Smoke"));
    assertThat(paymentRequest.getPayment().getBilling().getAddress().getPostalCode(),
        is("EC1N 2TD"));
  }

  @Test
  void createPayment_badInput_ShouldReturnException() {
    //Arrange
    when(paymentRequestMapper.toPaymentRequestDto(
        any())).thenReturn(new PaymentRequestDto());
    when(paymentsClient.createPayment(any())).thenThrow(WebClientResponseException.class);

    //Act
    Exception exception = assertThrows(WebClientResponseException.class, () ->
        paymentOutPort.createPayment(null)
    );

    //Assert
    assertThat(exception, notNullValue());
    verify(paymentsClient, times(1)).createPayment(any());
  }

  @Test
  void createMitCcPayment__ShouldReturnOk() {
    //Arrange
    when(paymentRequestMapper.toPaymentRequestDto(any(PaymentRequest.class)))
        .thenReturn(mockPaymentRequestDto());
    when(paymentsClient.createMitCcPayment(any(PaymentRequestDto.class)))
        .thenReturn(mockPaymentResponseDto());
    when(paymentResponseMapper.toModel(any(PaymentResponseDto.class)))
        .thenReturn(mockPaymentResponse());

    //Act
    var paymentResponse = paymentOutPort.createMitCcPayment(mockPaymentRequest());

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getPaymentId(), is("04230602742D"));
    verify(paymentsClient, times(1)).createMitCcPayment(any(PaymentRequestDto.class));
  }

  @Test
  void createMitCcPayment_badInput_ShouldReturnException() {
    //Arrange
    when(paymentRequestMapper.toPaymentRequestDto(any()))
        .thenReturn(new PaymentRequestDto());
    when(paymentsClient.createMitCcPayment(any()))
        .thenThrow(WebClientResponseException.class);

    //Act
    Exception exception = assertThrows(WebClientResponseException.class, () ->
        paymentOutPort.createMitCcPayment(null)
    );

    //Assert
    assertThat(exception, notNullValue());
    verify(paymentsClient, times(1)).createMitCcPayment(any());
  }

  @Test
  void updateTokenTest_shouldReturnOk() {
    var updateTokenRequestDto = mockUpdateTokenRequestDto();
    when(updateTokenMapper.toDto(any())).thenReturn(updateTokenRequestDto);
    when(paymentsClient.updateToken(updateTokenRequestDto)).thenReturn(
        mockUpdateTokenResponseDto());
    when(updateTokenMapper.toModel(any())).thenReturn(
        UpdateTokenResponse.builder().token("4764776852337921111").build());

    var updateTokenResponse = paymentOutPort.updateToken(mockUpdateTokenRequest());

    verify(paymentsClient, times(1)).updateToken(updateTokenRequestDto);
    assertThat(updateTokenResponse, notNullValue());
    assertThat(updateTokenResponse.getToken(), is("4764776852337921111"));

  }

  private UpdateTokenResponseDto mockUpdateTokenResponseDto() {
    var updateTokenResponseDto = new UpdateTokenResponseDto();
    updateTokenResponseDto.setToken("4764776852337921111");
    return updateTokenResponseDto;
  }

  private UpdateTokenRequestDto mockUpdateTokenRequestDto() {
    return new UpdateTokenRequestDto();
  }

  private UpdateTokenRequest mockUpdateTokenRequest() {
    return UpdateTokenRequest.builder()
        .requestId("1234")
        .token("4764776852337921111")
        .build();
  }

  private PaymentResponseDto mockPaymentResponseDto() {
    return new PaymentResponseDto().paymentId("123");
  }

  private PaymentRequestDto mockPaymentRequestDto() {
    return new PaymentRequestDto().requestId("123");
  }

  private PaymentRequest mockPaymentRequest() {
    return PaymentRequest.builder()
        .requestId("123")
        .build();
  }

  private ThreeCResponse mockThreecResponse() {
    return ThreeCResponse.builder().template("wb_newcard_pn_v3.xml").iPageHtml(
            "PCFET0NUWVBFIGh0bWw+CjxodG1sPgo8Ym9keSBzdHlsZT0icG9zaXRpb246IGFic29sdXRlOyB0b3A6IDUwJTsgdHJhbnNmb3JtOiB0cmFu")
        .build();
  }

  private PaymentResponse mockPaymentResponse() {
    return PaymentResponse.builder().paymentId("04230602742D")
        .providerResponse(ProviderResponse.builder().threecResponse(mockThreecResponse()).build())
        .build();
  }

  private Payment getPayment() {
    return Payment.builder()
        .billing(Billing.builder()
            .address(Address.builder()
                .countryCode("GB")
                .line1("ADDRESS LINE 1")
                .cityName("Big Smoke")
                .postalCode("EC1N 2TD")
                .build())
            .email("test@whitbread.com")
            .firstName("Smith")
            .lastName("Will")
            .title("Mr")
            .build())
        .card(Card.builder()
            .token("4216333880397891103")
            .build())
        .environment("https://www.premierinn.com")
        .subType("ECOMM")
        .type("CARD")
        .build();
  }

  private Booking getBooking() {
    return Booking.builder()
        .arrivalDate("2022-10-01")
        .businessSite(BusinessSite.builder()
            .identifier("DUNGOU")
            .location("Dundee")
            .name("Dundee West")
            .type("HOTEL")
            .build())
        .channel("PI")
        .departureDate("2022-10-02")
        .journey("BOOKING")
        .language("en")
        .rooms(of(RoomType.builder()
            .adultsNumber(2)
            .rate("FLEX")
            .type("DB")
            .build()))
        .type("PAY_NOW")
        .build();
  }

  private PaymentRequest getPaymentRequest() {
    return PaymentRequest.builder()
        .payment(getPayment())
        .booking(getBooking())
        .requestId("123")
        .build();
  }

}