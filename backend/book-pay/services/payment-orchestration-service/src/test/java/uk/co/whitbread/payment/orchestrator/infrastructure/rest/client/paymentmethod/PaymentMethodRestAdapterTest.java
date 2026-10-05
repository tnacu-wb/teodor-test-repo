package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.paymentmethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.ServerSocket;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.GatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.PaymentMethodProperties;

class PaymentMethodRestAdapterTest {

  private MockWebServer mockWebServer;
  private PaymentMethodRestAdapter adapter;

  private static final String BASKET_REFERENCE = "AQN-147756bb-bb71-4842-959a-2efe87e378ed";
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  private static final String USER_TYPE = "LEISURE";
  private static final String CLIENT_CHANNEL = "PI";

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    RestClient restClient = RestClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    PaymentMethodProperties properties = new PaymentMethodProperties();
    properties.setPaymentMethodsEndpoint("/v1/payment-methods");

    adapter = new PaymentMethodRestAdapter(restClient, properties);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Nested
  class RequestConstruction {

    @Test
    void sendsGetRequestWithCorrectPathAndQueryParameters() throws InterruptedException {
      mockWebServer.enqueue(jsonResponse(200, """
          [{"name":"CARD","type":"NEW_CARD","paymentProvider":"Datatrans",
            "enabled":true,"acceptedCardTypes":[{"type":"VIS"}]}]
          """));

      adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL);

      RecordedRequest request = mockWebServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getPath())
          .startsWith("/v1/payment-methods?")
          .contains("basketReference=" + BASKET_REFERENCE)
          .contains("country=" + COUNTRY)
          .contains("language=" + LANGUAGE)
          .contains("userType=" + USER_TYPE)
          .contains("clientChannel=" + CLIENT_CHANNEL);
    }
  }

  @Nested
  class SuccessfulValidation {

    @Test
    void returnsAvailableWithCardBrandsWhenNewCardDatatransMethodPresent() {
      mockWebServer.enqueue(jsonResponse(200, """
          [
            {
              "name": "CARD",
              "type": "NEW_CARD",
              "paymentProvider": "Datatrans",
              "enabled": true,
              "acceptedCardTypes": [
                {"type": "VIS"},
                {"type": "ECA"},
                {"type": "AMX"},
                {"type": "DIN"}
              ]
            },
            {
              "name": "PIBA",
              "type": "NEW_PIBA",
              "paymentProvider": "3CP",
              "enabled": true,
              "acceptedCardTypes": [{"type": "PI"}]
            }
          ]
          """));

      PaymentMethodValidationResult result = adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL);

      assertThat(result.cardPaymentAvailable()).isTrue();
      assertThat(result.availableCardBrands())
          .containsExactlyInAnyOrder("VIS", "ECA", "AMX", "DIN");
    }

    @Test
    void deduplicatesCardBrands() {
      mockWebServer.enqueue(jsonResponse(200, """
          [
            {
              "name": "CARD",
              "type": "NEW_CARD",
              "paymentProvider": "Datatrans",
              "enabled": true,
              "acceptedCardTypes": [
                {"type": "VIS"},
                {"type": "VIS"},
                {"type": "ECA"},
                {"type": "ECA"}
              ]
            }
          ]
          """));

      PaymentMethodValidationResult result = adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL);

      assertThat(result.cardPaymentAvailable()).isTrue();
      assertThat(result.availableCardBrands()).containsExactly("VIS", "ECA");
    }
  }

  @Nested
  class NewCardNotAvailable {

    @Test
    void returnsUnavailableWhenNoCardNewCardEntryExists() {
      mockWebServer.enqueue(jsonResponse(200, """
          [
            {
              "name": "PIBA",
              "type": "NEW_PIBA",
              "paymentProvider": "3CP",
              "enabled": true,
              "acceptedCardTypes": [{"type": "PI"}]
            },
            {
              "name": "APPLE",
              "type": "AP",
              "paymentProvider": "3CP",
              "enabled": true,
              "acceptedCardTypes": [{"type": "VS"}]
            }
          ]
          """));

      PaymentMethodValidationResult result = adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL);

      assertThat(result.cardPaymentAvailable()).isFalse();
      assertThat(result.availableCardBrands()).isEmpty();
    }

    @Test
    void returnsUnavailableWhenNewCardEntryIsDisabled() {
      mockWebServer.enqueue(jsonResponse(200, """
          [
            {
              "name": "CARD",
              "type": "NEW_CARD",
              "paymentProvider": "Datatrans",
              "enabled": false,
              "acceptedCardTypes": [
                {"type": "VIS"},
                {"type": "ECA"}
              ]
            }
          ]
          """));

      PaymentMethodValidationResult result = adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL);

      assertThat(result.cardPaymentAvailable()).isFalse();
      assertThat(result.availableCardBrands()).isEmpty();
    }

    @Test
    void returnsUnavailableWhenNewCardProviderIsNotDatatrans() {
      mockWebServer.enqueue(jsonResponse(200, """
          [
            {
              "name": "CARD",
              "type": "NEW_CARD",
              "paymentProvider": "3CP",
              "enabled": true,
              "acceptedCardTypes": [
                {"type": "VIS"},
                {"type": "ECA"}
              ]
            }
          ]
          """));

      PaymentMethodValidationResult result = adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL);

      assertThat(result.cardPaymentAvailable()).isFalse();
      assertThat(result.availableCardBrands()).isEmpty();
    }

    @Test
    void returnsUnavailableWhenResponseIsEmptyArray() {
      mockWebServer.enqueue(jsonResponse(200, "[]"));

      PaymentMethodValidationResult result = adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL);

      assertThat(result.cardPaymentAvailable()).isFalse();
      assertThat(result.availableCardBrands()).isEmpty();
    }
  }

  @Nested
  class ServiceErrors {

    @Test
    void throwsServiceUnavailableExceptionOn500Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Internal Server Error\"}"));

      assertThatThrownBy(() -> adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Payment Method Entity Service error");
    }

    /**
     * A 400 is the service's answer, not an outage: retrying sends the same rejected request
     * again. It must surface as the terminal {@link GatewayException} so the workflow's
     * doNotRetry list stops it after one attempt.
     */
    @Test
    void throwsGatewayExceptionOn400Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(400)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Bad Request\"}"));

      assertThatThrownBy(() -> adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL))
          .isInstanceOf(GatewayException.class)
          .hasMessageContaining("Payment Method Entity Service error");
    }

    @Test
    void throwsGatewayExceptionOn404Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(404)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Not Found\"}"));

      assertThatThrownBy(() -> adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL))
          .isInstanceOf(GatewayException.class)
          .hasMessageContaining("Payment Method Entity Service error");
    }

    /**
     * 429 is the one 4xx that asks us to come back later, so it stays retryable.
     */
    @Test
    void throwsServiceUnavailableExceptionOn429Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(429)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Too Many Requests\"}"));

      assertThatThrownBy(() -> adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Payment Method Entity Service error");
    }

    @Test
    void throwsServiceUnavailableExceptionOn503Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(503)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Service Unavailable\"}"));

      assertThatThrownBy(() -> adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Payment Method Entity Service error");
    }

    @Test
    void throwsServiceUnavailableExceptionWhenServiceUnreachable() throws IOException {
      mockWebServer.shutdown();

      assertThatThrownBy(() -> adapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Payment Method Entity Service is unreachable");
    }

    @Test
    void throwsServiceUnavailableExceptionOnConnectionRefused() throws IOException {
      int freePort;
      try (ServerSocket socket = new ServerSocket(0)) {
        freePort = socket.getLocalPort();
      }

      RestClient unreachableClient = RestClient.builder()
          .baseUrl("http://localhost:" + freePort)
          .build();

      PaymentMethodProperties unreachableProperties = new PaymentMethodProperties();
      unreachableProperties.setPaymentMethodsEndpoint("/v1/payment-methods");

      PaymentMethodRestAdapter unreachableAdapter =
          new PaymentMethodRestAdapter(unreachableClient, unreachableProperties);

      assertThatThrownBy(() -> unreachableAdapter.validatePaymentMethods(
          BASKET_REFERENCE, COUNTRY, LANGUAGE, USER_TYPE, CLIENT_CHANNEL))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Payment Method Entity Service is unreachable");
    }
  }

  private MockResponse jsonResponse(int statusCode, String body) {
    return new MockResponse()
        .setResponseCode(statusCode)
        .setHeader("Content-Type", "application/json")
        .setBody(body);
  }
}
