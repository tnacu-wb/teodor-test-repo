package uk.co.whitbread.payments.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.ProviderResponse;
import uk.co.whitbread.payments.model.ThreeCResponse;
import uk.co.whitbread.payments.model.WebHookEckohFormData;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class DefaultEckohPaymentWebhookServiceTest {
  @Mock
  private PaymentRepository paymentRepository;

  @InjectMocks
  private DefaultEckohPaymentWebhookService eckohPaymentWebhookService;

  @Test
  void testEckohWebhook() {
    var eckohWebHook = getWebhookEckohFormData();
    PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
    paymentsSchema.setPaymentId(eckohWebHook.getPaymentId());
    var threecResponse = getThreecResponse(eckohWebHook);
    var providerResponse = ProviderResponse.builder()
        .threeCResponse(threecResponse)
        .build();
    when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
    when(paymentRepository.updatePaymentResource(any(), any())).thenReturn(Mono.just(paymentsSchema));
    var paymentResponse = eckohPaymentWebhookService.handleWebhook(getServerRequest(eckohWebHook)).block();
    assertNotNull(paymentResponse);
    assertEquals(eckohWebHook.getReference(), paymentResponse.getPaymentId());
  }
  private WebHookEckohFormData getWebhookEckohFormData() {
    var eckohFormData = new WebHookEckohFormData();
    eckohFormData.setResult("success");
    eckohFormData.setResultCode("100");
    eckohFormData.setPaymentId("7289299294");
    eckohFormData.setMaskedPan("444433XXXXXX1111");
    eckohFormData.setExpiryDate("0616");
    eckohFormData.setScheme("visa");
    eckohFormData.setType("credit");
    eckohFormData.setReference("h-20151119164950");
    eckohFormData.setToken("q8LHkSdcxdtSSYe");
    return eckohFormData;
  }

  private ThreeCResponse getThreecResponse(WebHookEckohFormData webHookEckohFormData) {
    return ThreeCResponse.builder()
        .token(webHookEckohFormData.getToken())
        .expiry(webHookEckohFormData.getExpiryDate())
        .build();
  }

  private ServerRequest getServerRequest(WebHookEckohFormData webHookEckohFormData) {
    return MockServerRequest.builder()
        .body(Mono.just(webHookEckohFormData));
  }
}
