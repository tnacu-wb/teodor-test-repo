package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransWebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.WebhookInPort;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.PaymentGlobalExceptionHandler;

/**
 * Unit tests for the WebhookController Datatrans endpoint
 * (POST /api/payments/webhooks/datatrans).
 *
 * <p>Validates: Requirements 5.3, 5.5, 5.6
 */
@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

  private static final String WEBHOOK_URL = "/api/payments/webhooks/datatrans";
  private static final String SIGNATURE_HEADER = "Datatrans-Signature";
  private static final String BASKET_ID = "AQN-147756bb-bb71-4842-959a-2efe87e378ed";
  private static final String TRANSACTION_ID = "test-transaction-id";
  private static final String VALID_SIGNATURE = "t=1700000000000,s0=deadbeef";

  private static final String AUTHORIZED_PAYLOAD = """
      {
        "transactionId": "test-transaction-id",
        "merchantId": "test-merchant-id",
        "type": "payment",
        "status": "authorized",
        "currency": "GBP",
        "refno": "TEST-BOOKING-REF",
        "paymentMethod": "VIS",
        "authorizedAmount": 9900,
        "card": {
          "alias": "test-card-alias",
          "masked": "000000xxxxxx0000",
          "expiryMonth": "12",
          "expiryYear": "29",
          "info": {
            "brand": "TEST_BRAND",
            "type": "credit",
            "usage": "consumer",
            "country": "GB",
            "issuer": "TEST ISSUER"
          }
        },
        "attempts": [
          {
            "attemptId": "test-attempt-id",
            "type": "authorize",
            "amount": 9900,
            "acquirerAuthorizationCode": "000000",
            "status": "authorized",
            "paymentMethod": "VIS"
          }
        ]
      }""";

  private MockMvc mockMvc;

  @Mock
  private WebhookSignatureValidator signatureValidator;

  @Mock
  private WebhookInPort webhookInPort;

  @BeforeEach
  void setUp() {
    // Deliberately a strict mapper (FAIL_ON_UNKNOWN_PROPERTIES explicitly enabled, which
    // Jackson 3 disables by default) rather than a copy of Spring Boot's lenient
    // auto-configured one. Tolerance of unknown Datatrans fields is declared on
    // MobileSdkWebhookRequest itself, so the tests must fail if that annotation is removed
    // instead of being masked by mapper configuration.
    ObjectMapper objectMapper = JsonMapper.builder()
        .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
    WebhookController controller =
        new WebhookController(signatureValidator, webhookInPort, objectMapper);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new PaymentGlobalExceptionHandler())
        .build();
  }

  @Nested
  class AcceptedWebhook {

    @Test
    void validSignatureAndPayload_returns200AndSignalsWorkflow() throws Exception {
      when(signatureValidator.isValid(any(), any())).thenReturn(true);

      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", BASKET_ID)
              .header(SIGNATURE_HEADER, VALID_SIGNATURE)
              .contentType(MediaType.APPLICATION_JSON)
              .content(AUTHORIZED_PAYLOAD))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("received"));

      ArgumentCaptor<WebhookPayload> payloadCaptor =
          ArgumentCaptor.forClass(WebhookPayload.class);
      verify(webhookInPort).handleWebhook(eq(BASKET_ID), payloadCaptor.capture());

      DatatransWebhookPayload payload = (DatatransWebhookPayload) payloadCaptor.getValue();
      assertThat(payload.transactionId()).isEqualTo(TRANSACTION_ID);
      assertThat(payload.status()).isEqualTo("authorized");
      assertThat(payload.refno()).isEqualTo("TEST-BOOKING-REF");
      assertThat(payload.authorizedAmount()).isEqualTo(9900);
      assertThat(payload.cardAlias()).isEqualTo("test-card-alias");
      assertThat(payload.maskedCardNumber()).isEqualTo("000000xxxxxx0000");
      assertThat(payload.expiryMonth()).isEqualTo("12");
      assertThat(payload.expiryYear()).isEqualTo("29");
      assertThat(payload.acquirerAuthorizationCode()).isEqualTo("000000");
    }

    @Test
    void signedBodyIsValidatedBeforeDeserialization() throws Exception {
      when(signatureValidator.isValid(any(), any())).thenReturn(true);

      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", BASKET_ID)
              .header(SIGNATURE_HEADER, VALID_SIGNATURE)
              .contentType(MediaType.APPLICATION_JSON)
              .content(AUTHORIZED_PAYLOAD))
          .andExpect(status().isOk());

      verify(signatureValidator).isValid(AUTHORIZED_PAYLOAD, VALID_SIGNATURE);
    }

    @Test
    void unknownPayloadFields_areIgnoredAndWebhookAccepted() throws Exception {
      when(signatureValidator.isValid(any(), any())).thenReturn(true);

      String payloadWithUnknownFields = """
          {
            "transactionId": "test-transaction-id",
            "status": "authorized",
            "someFutureTopLevelField": "ignored",
            "card": {
              "alias": "test-card-alias",
              "someFutureCardField": "ignored",
              "info": {"brand": "TEST_BRAND", "someFutureInfoField": "ignored"}
            },
            "attempts": [
              {"attemptId": "test-attempt-id", "someFutureAttemptField": "ignored"}
            ]
          }""";

      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", BASKET_ID)
              .header(SIGNATURE_HEADER, VALID_SIGNATURE)
              .contentType(MediaType.APPLICATION_JSON)
              .content(payloadWithUnknownFields))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("received"));

      ArgumentCaptor<WebhookPayload> payloadCaptor =
          ArgumentCaptor.forClass(WebhookPayload.class);
      verify(webhookInPort).handleWebhook(eq(BASKET_ID), payloadCaptor.capture());

      // The known fields still bind at every nesting level the unknown fields appeared at.
      DatatransWebhookPayload payload = (DatatransWebhookPayload) payloadCaptor.getValue();
      assertThat(payload.transactionId()).isEqualTo(TRANSACTION_ID);
      assertThat(payload.cardAlias()).isEqualTo("test-card-alias");
    }

    @Test
    void mapsCardDetailsAndAttemptsToDomainModel() throws Exception {
      when(signatureValidator.isValid(any(), any())).thenReturn(true);

      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", BASKET_ID)
              .header(SIGNATURE_HEADER, VALID_SIGNATURE)
              .contentType(MediaType.APPLICATION_JSON)
              .content(AUTHORIZED_PAYLOAD))
          .andExpect(status().isOk());

      ArgumentCaptor<WebhookPayload> payloadCaptor =
          ArgumentCaptor.forClass(WebhookPayload.class);
      verify(webhookInPort).handleWebhook(eq(BASKET_ID), payloadCaptor.capture());

      DatatransWebhookPayload payload = (DatatransWebhookPayload) payloadCaptor.getValue();
      assertThat(payload.merchantId()).isEqualTo("test-merchant-id");
      assertThat(payload.currency()).isEqualTo("GBP");
      assertThat(payload.paymentMethod()).isEqualTo("VIS");
    }
  }

  @Nested
  class RejectedSignature {

    @Test
    void invalidSignature_returns401AndDoesNotSignalWorkflow() throws Exception {
      when(signatureValidator.isValid(any(), any())).thenReturn(false);

      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", BASKET_ID)
              .header(SIGNATURE_HEADER, "t=1700000000000,s0=notthesignature")
              .contentType(MediaType.APPLICATION_JSON)
              .content(AUTHORIZED_PAYLOAD))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.status").value("unauthorized"));

      verifyNoInteractions(webhookInPort);
    }

    @Test
    void missingSignatureHeader_returns401AndDoesNotSignalWorkflow() throws Exception {
      when(signatureValidator.isValid(any(), isNull())).thenReturn(false);

      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", BASKET_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(AUTHORIZED_PAYLOAD))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.status").value("unauthorized"));

      verifyNoInteractions(webhookInPort);
    }
  }

  @Nested
  class MalformedBody {

    @Test
    void emptyBody_returns400() throws Exception {
      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", BASKET_ID)
              .header(SIGNATURE_HEADER, VALID_SIGNATURE)
              .contentType(MediaType.APPLICATION_JSON)
              .content(""))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value("invalid"));

      verifyNoInteractions(signatureValidator);
      verifyNoInteractions(webhookInPort);
    }

    @Test
    void unreadableJson_returns400AndDoesNotSignalWorkflow() throws Exception {
      when(signatureValidator.isValid(any(), any())).thenReturn(true);

      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", BASKET_ID)
              .header(SIGNATURE_HEADER, VALID_SIGNATURE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("{not-valid-json"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value("invalid"));

      verify(webhookInPort, never()).handleWebhook(any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{}",
        "{\"status\": \"authorized\"}",
        "{\"transactionId\": \"test-transaction-id\"}",
        "{\"transactionId\": \"   \", \"status\": \"authorized\"}"
    })
    void missingRequiredFields_returns400AndDoesNotSignalWorkflow(String body) throws Exception {
      when(signatureValidator.isValid(any(), any())).thenReturn(true);

      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", BASKET_ID)
              .header(SIGNATURE_HEADER, VALID_SIGNATURE)
              .contentType(MediaType.APPLICATION_JSON)
              .content(body))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value("invalid"));

      verify(webhookInPort, never()).handleWebhook(any(), any());
    }
  }

  @Nested
  class UnresolvedWorkflow {

    @Test
    void missingBasketIdParam_returns200WithNullCorrelationKey() throws Exception {
      when(signatureValidator.isValid(any(), any())).thenReturn(true);

      mockMvc.perform(post(WEBHOOK_URL)
              .header(SIGNATURE_HEADER, VALID_SIGNATURE)
              .contentType(MediaType.APPLICATION_JSON)
              .content(AUTHORIZED_PAYLOAD))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("received"));

      verify(webhookInPort).handleWebhook(isNull(), any());
    }

    @Test
    void blankBasketIdParam_returns200AndDelegatesWithoutProgression() throws Exception {
      when(signatureValidator.isValid(any(), any())).thenReturn(true);

      mockMvc.perform(post(WEBHOOK_URL)
              .param("basketId", "   ")
              .header(SIGNATURE_HEADER, VALID_SIGNATURE)
              .contentType(MediaType.APPLICATION_JSON)
              .content(AUTHORIZED_PAYLOAD))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("received"));

      // The blank correlation key reaches the port, which ignores it (no workflow signal);
      // the endpoint still acknowledges with 200 so Datatrans does not treat it as a failure.
      verify(webhookInPort).handleWebhook(eq("   "), any());
    }
  }
}
