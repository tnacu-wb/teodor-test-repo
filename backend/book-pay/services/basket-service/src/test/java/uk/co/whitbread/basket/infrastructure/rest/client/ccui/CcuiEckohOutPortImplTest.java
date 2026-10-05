package uk.co.whitbread.basket.infrastructure.rest.client.ccui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohAddress;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohAgent;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohAmount;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohBilling;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohBooking;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohBusinessSite;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPayment;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.Billing;
import uk.co.whitbread.basket.domain.model.payments.out.Booking;
import uk.co.whitbread.basket.domain.model.payments.out.Card;
import uk.co.whitbread.basket.domain.model.payments.out.Payment;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.CcuiEckohOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper.CcuiEckohPaymentMapper;

@ExtendWith(MockitoExtension.class)
class CcuiEckohOutPortImplTest {

  private final static String REFERENCE = "123";
  private final static String PAYMENT_ID = "456543D";
  @Mock
  private CcuiEckohPaymentMapper ccuiEckohPaymentMapper;
  @InjectMocks
  private CcuiEckohOutPortImpl ccuiEckohOutPort;

  @Test
  void getPaymentRequest_shouldReturnOk() {
    when(ccuiEckohPaymentMapper.toPaymentRequestModel(createPaymentResponse())).thenReturn(
        createPaymentRequest());

    //ACT
    var response = ccuiEckohOutPort.createPaymentRequest(createPaymentResponse());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getRequestId(), is("ECKOH-VERIFICATION-" + REFERENCE));
    assertThat(response.getBooking().getJourney(), is("SECURITY_CHECK"));
    assertThat(response.getBooking().getReference(), is(REFERENCE));
    verifyNoMoreInteractions(ccuiEckohPaymentMapper);
  }

  @Test
  void createPaymentRequest_shouldReturnOk() {
    when(ccuiEckohPaymentMapper.toPaymentRequestModel(createEckohPaymentRequest())).thenReturn(
        createPaymentRequestEckoh());

    var response = ccuiEckohOutPort.createPaymentRequest(createEckohPaymentRequest());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getRequestId(), is(createPaymentRequestEckoh().getRequestId()));
    assertThat(response.getPayment().getType(),
        is(createPaymentRequestEckoh().getPayment().getType()));
    assertThat(response.getPayment().getEnvironment(),
        is(createPaymentRequestEckoh().getPayment().getEnvironment()));
    assertThat(response.getPayment().getSubType(),
        is(createPaymentRequestEckoh().getPayment().getSubType()));
    verifyNoMoreInteractions(ccuiEckohPaymentMapper);
  }

  private PaymentRequest createPaymentRequestEckoh() {
    return PaymentRequest.builder()
        .requestId("test")
        .payment(uk.co.whitbread.basket.domain.model.payments.in.Payment.builder()
            .amount(Amount.builder()
                .currency("GBP")
                .minorUnits(BigDecimal.ZERO)
                .build())
            .environment("N/A")
            .subType("ECKOH")
            .type("ECKOH")
            .build())
        .build();
  }

  private EckohPaymentRequest createEckohPaymentRequest() {
    return EckohPaymentRequest.builder()
        .requestId("test")
        .payment(EckohPayment.builder()
            .amount(EckohAmount.builder()
                .currency("GBP")
                .minorUnits(0)
                .build())
            .environment("N/A")
            .subType("ECKOH")
            .type("ECKOH")
            .billing(EckohBilling.builder()
                .address(EckohAddress.builder()
                    .postalCode("N/A")
                    .line1("N/A")
                    .cityName("Some City")
                    .build())
                .build())
            .build())
        .booking(EckohBooking.builder()
            .language("en")
            .channel("CCUI")
            .agent(EckohAgent.builder()
                .name("TEST")
                .email("test@test.com")
                .build())
            .businessSite(EckohBusinessSite.builder()
                .type("type")
                .identifier("iden")
                .name("name")
                .build())
            .build())
        .build();
  }

  private PaymentRequest createPaymentRequest() {
    return PaymentRequest.builder()
        .requestId("ECKOH-VERIFICATION-" + REFERENCE)
        .booking(uk.co.whitbread.basket.domain.model.payments.in.Booking.builder()

            .journey("SECURITY_CHECK")
            .channel("CCUI")
            .type("type")
            .businessSite(BusinessSite.builder()
                .identifier("iden")
                .type("type")
                .build())
            .reference(REFERENCE)
            .build())
        .payment(uk.co.whitbread.basket.domain.model.payments.in.Payment.builder()
            .type("type")
            .subType("subType")
            .billing(uk.co.whitbread.basket.domain.model.payments.in.Billing.builder()
                .email("test")
                .build())
            .card(uk.co.whitbread.basket.domain.model.payments.in.Card.builder()
                .expiryMonth("02")
                .expiryYear("24")
                .token("xx2232")
                .build())
            .build())
        .build();
  }

  private PaymentResponse createPaymentResponse() {
    return PaymentResponse.builder()
        .paymentId(PAYMENT_ID)
        .bookingReference(REFERENCE)
        .booking(Booking.builder()
            .journey("SECURITY_CHECK")
            .reference(REFERENCE)
            .build())
        .payment(createPayment())
        .build();
  }

  private Payment createPayment() {
    return Payment.builder()
        .billing(Billing.builder()
            .email("test")
            .build()
        )
        .card(Card.builder()
            .expiryMonth("02")
            .expiryYear("24")
            .token("xx2232")
            .build())
        .build();
  }
}
