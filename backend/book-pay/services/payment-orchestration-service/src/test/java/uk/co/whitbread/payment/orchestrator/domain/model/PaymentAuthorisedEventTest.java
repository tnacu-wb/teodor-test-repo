package uk.co.whitbread.payment.orchestrator.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Unit tests for {@link PaymentAuthorisedEvent} JSON serialization.
 *
 * <p>Validates: Requirements 8.1
 */
class PaymentAuthorisedEventTest {

  private final ObjectMapper objectMapper = JsonMapper.builder().build();

  @Nested
  class Serialization {

    @Test
    void serializesAllFieldsToJson() throws Exception {
      var event = new PaymentAuthorisedEvent(
          "basket-1", "txn-123", "datatrans", "VIS",
          "alias-token", "4242", "12/28", 8600, "GBP", PaymentOption.PAY_NOW,
          "AUTHORIZED", "de");

      String json = objectMapper.writeValueAsString(event);

      assertThat(json)
          .contains("\"basketId\":\"basket-1\"")
          .contains("\"transactionId\":\"txn-123\"")
          .contains("\"paymentProvider\":\"datatrans\"")
          .contains("\"paymentMethod\":\"VIS\"")
          .contains("\"cardAlias\":\"alias-token\"")
          .contains("\"last4Digits\":\"4242\"")
          .contains("\"expiry\":\"12/28\"")
          .contains("\"authorizedAmount\":8600")
          .contains("\"currency\":\"GBP\"")
          .contains("\"paymentStatus\":\"AUTHORIZED\"")
          .contains("\"language\":\"de\"");
    }

    @Test
    void serializesNullFieldsAsJsonNull() throws Exception {
      var event = new PaymentAuthorisedEvent(
          "basket-1", "txn-123", "datatrans", null,
          null, null, null, 0L, null, null, "AUTHORIZED", null);

      String json = objectMapper.writeValueAsString(event);

      assertThat(json)
          .contains("\"basketId\":\"basket-1\"")
          .contains("\"transactionId\":\"txn-123\"")
          .contains("\"paymentProvider\":\"datatrans\"")
          .contains("\"paymentMethod\":null")
          .contains("\"cardAlias\":null")
          .contains("\"last4Digits\":null")
          .contains("\"expiry\":null")
          .contains("\"authorizedAmount\":0")
          .contains("\"currency\":null");
    }
  }

  @Nested
  class Deserialization {

    @Test
    void roundTripsFullyPopulatedEvent() throws Exception {
      var original = new PaymentAuthorisedEvent(
          "basket-1", "txn-123", "datatrans", "VIS",
          "alias-token", "4242", "12/28", 8600, "GBP", PaymentOption.PAY_NOW,
          "AUTHORIZED", "de");

      String json = objectMapper.writeValueAsString(original);
      PaymentAuthorisedEvent deserialized =
          objectMapper.readValue(json, PaymentAuthorisedEvent.class);

      assertThat(deserialized).isEqualTo(original);
    }

    @Test
    void roundTripsEventWithNullFields() throws Exception {
      var original = new PaymentAuthorisedEvent(
          "basket-1", "txn-123", "datatrans", null,
          null, null, null, 0L, null, null, "AUTHORIZED", null);

      String json = objectMapper.writeValueAsString(original);
      PaymentAuthorisedEvent deserialized =
          objectMapper.readValue(json, PaymentAuthorisedEvent.class);

      assertThat(deserialized).isEqualTo(original);
    }

    @Test
    void toleratesUnknownFieldsForForwardCompatibility() throws Exception {
      String json = """
          {
            "basketId": "basket-1",
            "transactionId": "txn-123",
            "paymentProvider": "datatrans",
            "paymentMethod": "VIS",
            "cardAlias": "alias-token",
            "last4Digits": "4242",
            "expiry": "12/28",
            "authorizedAmount": 8600,
            "currency": "GBP",
            "futureField": "some-value"
          }
          """;

      PaymentAuthorisedEvent deserialized =
          objectMapper.readValue(json, PaymentAuthorisedEvent.class);

      assertThat(deserialized.basketId()).isEqualTo("basket-1");
      assertThat(deserialized.transactionId()).isEqualTo("txn-123");
      assertThat(deserialized.paymentProvider()).isEqualTo("datatrans");
      assertThat(deserialized.paymentMethod()).isEqualTo("VIS");
      assertThat(deserialized.cardAlias()).isEqualTo("alias-token");
      assertThat(deserialized.last4Digits()).isEqualTo("4242");
      assertThat(deserialized.expiry()).isEqualTo("12/28");
      assertThat(deserialized.authorizedAmount()).isEqualTo(8600);
      assertThat(deserialized.currency()).isEqualTo("GBP");
    }
  }
}
