package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.ProviderResponse;
import uk.co.whitbread.payments.model.ThreeCResponse;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.repository.PaymentRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payments.util.PaymentRequestFixtures.getServerRequest;
import static uk.co.whitbread.payments.util.PaymentRequestFixtures.getWebhookData;

@ExtendWith(MockitoExtension.class)
class DefaultPaymentWebhookServiceTest {

    private static final String FRAUD_CHECK_DECISION = "REJECT";
    private static final String FIRST_NAME = "Ashutosh";
    private static final String LAST_NAME = "Dhakate";
    private static final String EXPIRY = "02/24";

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private DefaultPaymentWebhookService paymentWebhookService;

    @Test
    void testWebhookWhenFraudScreenDisabled() {
        var multiValueMap = getWebhookData();
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        var threecResponse = ThreeCResponse.builder()
                .expiry(EXPIRY)
                .cardholderFirstName(
                        Base64.getEncoder().encodeToString(FIRST_NAME.getBytes(StandardCharsets.UTF_8)))
                .cardholderLastName(Base64.getEncoder().encodeToString(LAST_NAME.getBytes(StandardCharsets.UTF_8))).build();
        var providerResponse = ProviderResponse.builder().threeCResponse(threecResponse).build();
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        when(paymentRepository.updatePaymentResource(any(), any())).thenReturn(Mono.just(paymentsSchema));
        var paymentResponse = paymentWebhookService.handleWebhook(getServerRequest(multiValueMap, paymentId)).block();
        assertNotNull(paymentResponse);
        assertEquals(paymentId, paymentResponse.getPaymentId());
        assertNull(paymentResponse.getProviderResponse().getThreeCResponse().getFraudCheckDecision());
        assertEquals(FIRST_NAME, new String(Base64.getDecoder().decode(paymentResponse.getProviderResponse().getThreeCResponse().getCardholderFirstName()), StandardCharsets.UTF_8));
        assertEquals(LAST_NAME, new String(Base64.getDecoder().decode(paymentResponse.getProviderResponse().getThreeCResponse().getCardholderLastName()), StandardCharsets.UTF_8));
        assertEquals(EXPIRY, paymentResponse.getProviderResponse().getThreeCResponse().getExpiry());
    }

    @Test
    void testWebhookWhenFraudScreenEnabled() {
        var multiValueMap = getWebhookData();
        multiValueMap.put("fraud_check_decision", List.of(FRAUD_CHECK_DECISION));
        multiValueMap.put("fraud_check_result", List.of("1234"));
        multiValueMap.put("fraud_check_result_reason", List.of("APPROVED"));
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        var threecResponse = ThreeCResponse.builder().fraudCheckDecision(FRAUD_CHECK_DECISION).build();
        var providerResponse = ProviderResponse.builder().threeCResponse(threecResponse).build();
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        when(paymentRepository.updatePaymentResource(any(), any())).thenReturn(Mono.just(paymentsSchema));
        var paymentResponse = paymentWebhookService.handleWebhook(getServerRequest(multiValueMap, paymentId)).block();
        assertNotNull(paymentResponse);
        assertEquals(paymentId, paymentResponse.getPaymentId());
        assertEquals(FRAUD_CHECK_DECISION, paymentResponse.getProviderResponse().getThreeCResponse().getFraudCheckDecision());
    }

    @Test
    void testWebhookWhenTokenNoIsNull() {
        var multiValueMap = getWebhookData();
        multiValueMap.put("TokenNo", null);
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        var threecResponse = ThreeCResponse.builder()
                .token("").build();
        var providerResponse = ProviderResponse.builder().threeCResponse(threecResponse).build();
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        when(paymentRepository.updatePaymentResource(any(), any())).thenReturn(Mono.just(paymentsSchema));
        var paymentResponse = paymentWebhookService.handleWebhook(getServerRequest(multiValueMap, paymentId)).block();
        assertNotNull(paymentResponse);
        assertEquals("", paymentResponse.getProviderResponse().getThreeCResponse().getToken());
    }
}
