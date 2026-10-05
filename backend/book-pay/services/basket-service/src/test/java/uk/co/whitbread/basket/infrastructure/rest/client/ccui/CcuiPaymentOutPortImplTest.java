package uk.co.whitbread.basket.infrastructure.rest.client.ccui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.AddressCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.BillingCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiRequest;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.CcuiPaymentOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper.CcuiEckohPaymentMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper.CcuiEckohPaymentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.PaymentsClient;

@ExtendWith(MockitoExtension.class)
public class CcuiPaymentOutPortImplTest {

  @InjectMocks
  private CcuiPaymentOutPortImpl ccuiPaymentOutPort;
  @Mock
  private PaymentsClient paymentsClient;
  @Mock
  private CcuiEckohPaymentMapper ccuiEckohPaymentMapper;
  @Mock
  private CcuiEckohPaymentResponseMapper ccuiEckohPaymentResponseMapper;

  @Test
  void createPaymentRequestTest() {
    when(ccuiEckohPaymentMapper.toPaymentRequestModel(any(PaymentCcuiRequest.class))).thenReturn(createPaymentRequestForTesting());
    var response = ccuiPaymentOutPort.createPaymentRequest(mockPaymentRequest());
    assertThat(response, notNullValue());
    assertThat(response.getRequestId(), is("1234"));

  }

  @Test
  void getPaymentConfirmationTest() {
    when(ccuiEckohPaymentResponseMapper.toModel(any())).thenReturn(mockPaymentResponse());
    var response = ccuiPaymentOutPort.getPaymentConfirmation("123456");
    assertThat(response, notNullValue());
    assertThat(response.getPaymentId(), is("123456"));
  }

  private PaymentResponse mockPaymentResponse() {
   return PaymentResponse.builder().paymentId("123456").build();
  }

  private PaymentRequest createPaymentRequestForTesting() {
    return PaymentRequest.builder()
        .requestId("1234")
        .build();
  }

  private PaymentCcuiRequest mockPaymentRequest() {
    return PaymentCcuiRequest.builder()
        .payment(PaymentCcui.builder()
            .type("PIBA")
            .subType("MOTO")
            .environment("CCC")
            .billing(BillingCcui.builder()
                .address(AddressCcui.builder()
                    .postalCode("N/A")
                    .line1("N/A")
                    .cityName("Some City")
                    .build())
                .build())
            .amount(Amount.builder()
                .currency("EUR")
                .minorUnits(BigDecimal.ONE)
                .build())
            .build())
        .requestId("1234")
        .build();
  }
}
