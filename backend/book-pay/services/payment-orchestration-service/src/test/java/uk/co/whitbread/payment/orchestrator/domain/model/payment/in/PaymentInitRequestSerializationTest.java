package uk.co.whitbread.payment.orchestrator.domain.model.payment.in;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransWebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;

/**
 * Verifies Jackson polymorphic serialization/deserialization for payment request and webhook
 * payload hierarchies.
 *
 * <p>Uses a plain {@link JsonMapper} (matching Spring Boot 4 auto-configuration) to ensure the
 * {@code @JsonTypeInfo}/{@code @JsonSubTypes} annotations on the sealed interfaces produce
 * correct round-trip behavior without any additional ObjectMapper customization.
 */
class PaymentInitRequestSerializationTest {

  private final ObjectMapper objectMapper = JsonMapper.builder().build();

  @Nested
  class PaymentInitRequestHierarchy {

    @Test
    void newCardWebInitRequest_serializesWithDiscriminator() throws Exception {
      PaymentInitRequest request = new NewCardWebInitRequest(
          "basket-123", "https://www.premierinn.com/return",
          "gb", "en", "LEISURE", "PI");

      String json = objectMapper.writeValueAsString(request);

      assertThat(json).contains("\"paymentMethod\":\"NEW_CARD_WEB\"");
      assertThat(json).contains("\"basketId\":\"basket-123\"");
      assertThat(json).contains("\"returnUrl\":\"https://www.premierinn.com/return\"");
    }

    @Test
    void newCardWebInitRequest_roundTrip() throws Exception {
      PaymentInitRequest original = new NewCardWebInitRequest(
          "basket-123", "https://www.premierinn.com/return",
          "gb", "en", "LEISURE", "PI");

      String json = objectMapper.writeValueAsString(original);
      PaymentInitRequest deserialized = objectMapper.readValue(json, PaymentInitRequest.class);

      assertThat(deserialized).isInstanceOf(NewCardWebInitRequest.class);
      NewCardWebInitRequest web = (NewCardWebInitRequest) deserialized;
      assertThat(web.basketId()).isEqualTo("basket-123");
      assertThat(web.returnUrl()).isEqualTo("https://www.premierinn.com/return");
      assertThat(web.country()).isEqualTo("gb");
      assertThat(web.language()).isEqualTo("en");
      assertThat(web.userType()).isEqualTo("LEISURE");
      assertThat(web.clientChannel()).isEqualTo("PI");
      assertThat(web.paymentMethod()).isEqualTo(PaymentMethod.NEW_CARD_WEB);
    }

    @Test
    void newCardMobileInitRequest_serializesWithDiscriminator() throws Exception {
      PaymentInitRequest request = new NewCardMobileInitRequest(
          "basket-456", "gb", "en", "BUSINESS", "APPS_IOS");

      String json = objectMapper.writeValueAsString(request);

      assertThat(json).contains("\"paymentMethod\":\"NEW_CARD_MOBILE\"");
      assertThat(json).contains("\"basketId\":\"basket-456\"");
      assertThat(json).doesNotContain("returnUrl");
    }

    @Test
    void newCardMobileInitRequest_roundTrip() throws Exception {
      PaymentInitRequest original = new NewCardMobileInitRequest(
          "basket-456", "gb", "en", "BUSINESS", "APPS_IOS");

      String json = objectMapper.writeValueAsString(original);
      PaymentInitRequest deserialized = objectMapper.readValue(json, PaymentInitRequest.class);

      assertThat(deserialized).isInstanceOf(NewCardMobileInitRequest.class);
      NewCardMobileInitRequest mobile = (NewCardMobileInitRequest) deserialized;
      assertThat(mobile.basketId()).isEqualTo("basket-456");
      assertThat(mobile.country()).isEqualTo("gb");
      assertThat(mobile.language()).isEqualTo("en");
      assertThat(mobile.userType()).isEqualTo("BUSINESS");
      assertThat(mobile.clientChannel()).isEqualTo("APPS_IOS");
      assertThat(mobile.paymentMethod()).isEqualTo(PaymentMethod.NEW_CARD_MOBILE);
    }

    @Test
    void deserializeFromJson_newCardWeb() throws Exception {
      String json = """
          {
            "paymentMethod": "NEW_CARD_WEB",
            "basketId": "basket-web",
            "returnUrl": "https://premierinn.com/3ds",
            "country": "de",
            "language": "de",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }
          """;

      PaymentInitRequest deserialized = objectMapper.readValue(json, PaymentInitRequest.class);

      assertThat(deserialized).isInstanceOf(NewCardWebInitRequest.class);
      assertThat(deserialized.basketId()).isEqualTo("basket-web");
      assertThat(deserialized.paymentMethod()).isEqualTo(PaymentMethod.NEW_CARD_WEB);
    }

    @Test
    void deserializeFromJson_newCardMobile() throws Exception {
      String json = """
          {
            "paymentMethod": "NEW_CARD_MOBILE",
            "basketId": "basket-mobile",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "APPS_ANDROID"
          }
          """;

      PaymentInitRequest deserialized = objectMapper.readValue(json, PaymentInitRequest.class);

      assertThat(deserialized).isInstanceOf(NewCardMobileInitRequest.class);
      assertThat(deserialized.basketId()).isEqualTo("basket-mobile");
      assertThat(deserialized.paymentMethod()).isEqualTo(PaymentMethod.NEW_CARD_MOBILE);
    }
  }

  @Nested
  class WebhookPayloadHierarchy {

    @Test
    void datatransWebhookPayload_serializesWithGatewayDiscriminator() throws Exception {
      WebhookPayload payload = new DatatransWebhookPayload(
          "txn-789", "merchant-1", "authorized", "GBP",
          "ref-001", "VIS", 8600,
          "alias-token", "424242xxxxxx4242", "12", "28", "AUTH123");

      String json = objectMapper.writeValueAsString(payload);

      assertThat(json).contains("\"gateway\":\"DATATRANS\"");
      assertThat(json).contains("\"transactionId\":\"txn-789\"");
      assertThat(json).contains("\"status\":\"authorized\"");
    }

    @Test
    void datatransWebhookPayload_roundTrip() throws Exception {
      WebhookPayload original = new DatatransWebhookPayload(
          "txn-789", "merchant-1", "authorized", "GBP",
          "ref-001", "VIS", 8600,
          "alias-token", "424242xxxxxx4242", "12", "28", "AUTH123");

      String json = objectMapper.writeValueAsString(original);
      WebhookPayload deserialized = objectMapper.readValue(json, WebhookPayload.class);

      assertThat(deserialized).isInstanceOf(DatatransWebhookPayload.class);
      DatatransWebhookPayload dt = (DatatransWebhookPayload) deserialized;
      assertThat(dt.transactionId()).isEqualTo("txn-789");
      assertThat(dt.merchantId()).isEqualTo("merchant-1");
      assertThat(dt.status()).isEqualTo("authorized");
      assertThat(dt.currency()).isEqualTo("GBP");
      assertThat(dt.authorizedAmount()).isEqualTo(8600);
      assertThat(dt.cardAlias()).isEqualTo("alias-token");
    }

    @Test
    void deserializeFromJson_datatrans() throws Exception {
      String json = """
          {
            "gateway": "DATATRANS",
            "transactionId": "txn-abc",
            "merchantId": "merch-1",
            "status": "authorized",
            "currency": "EUR",
            "refno": "ref-999",
            "paymentMethod": "ECA",
            "authorizedAmount": 5000
          }
          """;

      WebhookPayload deserialized = objectMapper.readValue(json, WebhookPayload.class);

      assertThat(deserialized).isInstanceOf(DatatransWebhookPayload.class);
      assertThat(deserialized.transactionId()).isEqualTo("txn-abc");
      DatatransWebhookPayload dt = (DatatransWebhookPayload) deserialized;
      assertThat(dt.currency()).isEqualTo("EUR");
      assertThat(dt.authorizedAmount()).isEqualTo(5000);
    }
  }
}
