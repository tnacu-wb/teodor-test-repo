package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.GatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.model.BasketStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.BasketProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.ReservationProperties;

class ReservationServiceClientTest {

  private MockWebServer mockWebServer;
  private ReservationServiceClient client;
  private ReservationProperties properties;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    properties = new ReservationProperties();
    properties.setHost(mockWebServer.url("/").toString());
    properties.setReservationEndpoint("/v1/reservations/basket/{basketReference}");
    properties.setConnectTimeout(Duration.ofSeconds(5));
    properties.setReadTimeout(Duration.ofSeconds(10));

    RestClient restClient = RestClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    // The mapper interface uses a default method for all logic, so an anonymous
    // instance is sufficient (no MapStruct-generated abstract methods to implement)
    ReservationResponseMapper mapper = new ReservationResponseMapper() {};

    BasketProperties basketProperties = new BasketProperties();
    basketProperties.setHost(mockWebServer.url("/").toString());
    basketProperties.setChangeStatusEndpoint("/v1/baskets/{bookingReference}/changeStatus");
    basketProperties.setBasketEndpoint("/v1/baskets/{basketReference}");
    basketProperties.setConnectTimeout(Duration.ofSeconds(5));
    basketProperties.setReadTimeout(Duration.ofSeconds(10));

    RestClient basketRestClient = RestClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    client = new ReservationServiceClient(restClient, basketRestClient, properties, basketProperties, mapper);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Nested
  class HappyPath {

    @Test
    void returnsReservationWithBookingReferenceAsRefno() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(validResponseJson()));

      Reservation reservation = client.getReservation("bsk-a1b2c3d4");

      assertThat(reservation.basketId()).isEqualTo("bsk-a1b2c3d4");
      assertThat(reservation.hotelId()).isEqualTo("LONWAT");
      assertThat(reservation.totalCostOfStay()).isEqualByComparingTo("89.00");
      assertThat(reservation.currencyCode()).isEqualTo("GBP");
      assertThat(reservation.bookingReference()).isEqualTo("PI-123456789");
      assertThat(reservation.refno()).isEqualTo("PI-123456789");
      assertThat(reservation.channel()).isEqualTo("PI");
    }

    @Test
    void sendsAcceptJsonHeader() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(validResponseJson()));

      client.getReservation("bsk-a1b2c3d4");

      RecordedRequest request = mockWebServer.takeRequest();
      assertThat(request.getHeader("Accept")).isEqualTo("application/json");
    }

    @Test
    void substitutesBasketIdInPathParameter() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(validResponseJson()));

      client.getReservation("my-basket-123");

      RecordedRequest request = mockWebServer.takeRequest();
      assertThat(request.getPath())
          .isEqualTo("/v1/reservations/basket/my-basket-123");
    }
  }

  @Nested
  class ErrorResponses {

    @Test
    void throwsBasketNotFoundExceptionOn404() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(404)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Not Found\"}"));

      assertThatThrownBy(() -> client.getReservation("unknown-basket"))
          .isInstanceOf(BasketNotFoundException.class)
          .hasMessageContaining("unknown-basket");
    }

    @Test
    void throwsGatewayExceptionOn500() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Internal Server Error\"}"));

      assertThatThrownBy(() -> client.getReservation("bsk-a1b2c3d4"))
          .isInstanceOf(GatewayException.class)
          .hasMessageContaining("Reservation service returned");
    }
  }

  @Nested
  class ConnectionErrors {

    @Test
    void throwsServiceUnavailableExceptionOnConnectionFailure() throws IOException {
      mockWebServer.shutdown();

      assertThatThrownBy(() -> client.getReservation("bsk-a1b2c3d4"))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("unreachable");
    }
  }

  @Nested
  class EmptyReservationList {

    @Test
    void throwsBasketNotFoundExceptionWhenReservationByIdListIsEmpty() {
      String json = """
          {
            "reservationByIdList": [],
            "bookingReference": "PI-123456789",
            "basketReference": "bsk-a1b2c3d4",
            "hotelId": "LONWAT",
            "currencyCode": "GBP",
            "totalCost": 89.00,
          "channel": "PI"
          }
          """;

      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(json));

      assertThatThrownBy(() -> client.getReservation("bsk-a1b2c3d4"))
          .isInstanceOf(BasketNotFoundException.class)
          .hasMessageContaining("No reservations found");
    }
  }

  @Nested
  class MissingRequiredFields {

    @Test
    void throwsDatatransGatewayExceptionWhenBookingReferenceIsMissing() {
      String json = """
          {
            "reservationByIdList": [
              {
                "reservationId": "RES-001",
                "rateInfo": {
                  "summary": {
                    "totalCostOfStay": 89.00,
                    "currencyCode": "GBP"
                  }
                },
                "reservationStatus": "RESERVED"
              }
            ],
            "bookingReference": null,
            "basketReference": "bsk-a1b2c3d4",
            "hotelId": "LONWAT",
            "currencyCode": "GBP",
            "totalCost": 89.00,
          "channel": "PI"
          }
          """;

      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(json));

      assertThatThrownBy(() -> client.getReservation("bsk-a1b2c3d4"))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("bookingReference");
    }

    @Test
    void throwsDatatransGatewayExceptionWhenTotalCostOfStayIsMissing() {
      String json = """
          {
            "reservationByIdList": [
              {
                "reservationId": "RES-001",
                "rateInfo": {
                  "summary": {
                    "totalCostOfStay": null,
                    "currencyCode": "GBP"
                  }
                },
                "reservationStatus": "RESERVED"
              }
            ],
            "bookingReference": "PI-123456789",
            "basketReference": "bsk-a1b2c3d4",
            "hotelId": "LONWAT",
            "currencyCode": "GBP",
            "totalCost": 89.00,
          "channel": "PI"
          }
          """;

      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(json));

      assertThatThrownBy(() -> client.getReservation("bsk-a1b2c3d4"))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("totalCostOfStay");
    }

    @Test
    void throwsDatatransGatewayExceptionWhenCurrencyCodeIsMissing() {
      String json = """
          {
            "reservationByIdList": [
              {
                "reservationId": "RES-001",
                "rateInfo": {
                  "summary": {
                    "totalCostOfStay": 89.00,
                    "currencyCode": null
                  }
                },
                "reservationStatus": "RESERVED"
              }
            ],
            "bookingReference": "PI-123456789",
            "basketReference": "bsk-a1b2c3d4",
            "hotelId": "LONWAT",
            "currencyCode": "GBP",
            "totalCost": 89.00,
          "channel": "PI"
          }
          """;

      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(json));

      assertThatThrownBy(() -> client.getReservation("bsk-a1b2c3d4"))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("currencyCode");
    }
  }

  @Nested
  class InvalidBasketIdInput {

    @Test
    void throwsIllegalArgumentExceptionWhenBasketIdIsNull() {
      assertThatThrownBy(() -> client.getReservation(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("basketId must not be null or blank");
    }

    @Test
    void throwsIllegalArgumentExceptionWhenBasketIdIsBlank() {
      assertThatThrownBy(() -> client.getReservation("   "))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("basketId must not be null or blank");
    }
  }


  @Nested
  class ChangeBasketStatus {

    @Test
    void sendsCorrectRequestToBasketService() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse().setResponseCode(200));

      client.changeBasketStatus("ARH1234567", "PAY_PENDING");

      RecordedRequest request = mockWebServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("PUT");
      assertThat(request.getPath()).isEqualTo("/v1/baskets/ARH1234567/changeStatus");
      assertThat(request.getHeader("Content-Type")).contains("application/json");
      assertThat(request.getBody().readUtf8()).contains("PAY_PENDING");
    }

    @Test
    void throwsBasketNotFoundExceptionOn404() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(404)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Not Found\"}"));

      assertThatThrownBy(() -> client.changeBasketStatus("UNKNOWN123", "PAY_PENDING"))
          .isInstanceOf(BasketNotFoundException.class)
          .hasMessageContaining("UNKNOWN123");
    }

    @Test
    void throwsGatewayExceptionOn500() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Internal Server Error\"}"));

      assertThatThrownBy(() -> client.changeBasketStatus("ARH1234567", "PAY_PENDING"))
          .isInstanceOf(GatewayException.class)
          .hasMessageContaining("Basket service returned");
    }

    @Test
    void throwsServiceUnavailableExceptionOnConnectionFailure() throws IOException {
      mockWebServer.shutdown();

      assertThatThrownBy(() -> client.changeBasketStatus("ARH1234567", "PAY_PENDING"))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("unreachable");
    }

    @Test
    void throwsIllegalArgumentExceptionWhenBookingReferenceIsNull() {
      assertThatThrownBy(() -> client.changeBasketStatus(null, "PAY_PENDING"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("bookingReference must not be null or blank");
    }

    @Test
    void throwsIllegalArgumentExceptionWhenBookingReferenceIsBlank() {
      assertThatThrownBy(() -> client.changeBasketStatus("   ", "PAY_PENDING"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("bookingReference must not be null or blank");
    }
  }

  /**
   * The booking-completion safety net: when the {@code bookingCompleted} Kafka event does not
   * arrive, the payment workflow asks the Basket Service directly what happened.
   */
  @Nested
  class GetBasketStatus {

    @Test
    void readsTheBasketByReference() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(basketJson("COMPLETED")));

      BasketStatus status = client.getBasketStatus("bsk-a1b2c3d4");

      assertThat(status).isEqualTo(BasketStatus.COMPLETED);

      RecordedRequest request = mockWebServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getPath()).isEqualTo("/v1/baskets/bsk-a1b2c3d4");
      assertThat(request.getHeader("Accept")).isEqualTo("application/json");
    }

    @ParameterizedTest
    @ValueSource(strings = {"COMPLETED", "AMENDED", "PRE_CHECKED_IN", "PRE_CHECKED_OUT"})
    void readsTheStatusesThatMeanTheBookingExists(String value) {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(basketJson(value)));

      assertThat(client.getBasketStatus("bsk-a1b2c3d4").isBookingCompleted()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"FAILED", "CANCELLED", "AMEND_FAILED", "CIOL_FAILED",
        "CIOL_RC_FAILED", "SECURE_FAILED"})
    void readsTheStatusesThatMeanTheBookingWillNotHappen(String value) {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(basketJson(value)));

      assertThat(client.getBasketStatus("bsk-a1b2c3d4").isBookingFailed()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"OPEN", "PROCESSING", "PAY_PENDING", "AMENDING"})
    void readsTheStatusesThatMeanTheBookingIsStillDeciding(String value) {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(basketJson(value)));

      assertThat(client.getBasketStatus("bsk-a1b2c3d4").isPending()).isTrue();
    }

    @Test
    void treatsAStatusItDoesNotRecogniseAsStillPending() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(basketJson("SOME_NEW_STATUS")));

      // A status the basket team added after this service shipped is not evidence that the
      // booking failed, and the workflow must not cancel an authorization on a guess.
      BasketStatus status = client.getBasketStatus("bsk-a1b2c3d4");
      assertThat(status).isEqualTo(BasketStatus.UNKNOWN);
      assertThat(status.isPending()).isTrue();
    }

    @Test
    void ignoresTheRestOfTheBasketPayload() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody("""
              {
                "reference": "bsk-a1b2c3d4",
                "bookingReference": "PI-123456789",
                "status": "COMPLETED",
                "hotelId": "LONWAT",
                "items": [{"itemType": "ROOM"}],
                "somethingAddedNextQuarter": {"nested": true}
              }
              """));

      assertThat(client.getBasketStatus("bsk-a1b2c3d4")).isEqualTo(BasketStatus.COMPLETED);
    }

    @Test
    void throwsBasketNotFoundExceptionOn404() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(404)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Not Found\"}"));

      assertThatThrownBy(() -> client.getBasketStatus("unknown-basket"))
          .isInstanceOf(BasketNotFoundException.class)
          .hasMessageContaining("unknown-basket");
    }

    @Test
    void throwsGatewayExceptionOn500() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Internal Server Error\"}"));

      assertThatThrownBy(() -> client.getBasketStatus("bsk-a1b2c3d4"))
          .isInstanceOf(GatewayException.class)
          .hasMessageContaining("Basket service returned");
    }

    @Test
    void throwsServiceUnavailableExceptionOnConnectionFailure() throws IOException {
      mockWebServer.shutdown();

      assertThatThrownBy(() -> client.getBasketStatus("bsk-a1b2c3d4"))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("unreachable");
    }

    @Test
    void throwsIllegalArgumentExceptionWhenBasketIdIsBlank() {
      assertThatThrownBy(() -> client.getBasketStatus("   "))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("basketId must not be null or blank");
    }

    private String basketJson(String status) {
      return """
          {
            "reference": "bsk-a1b2c3d4",
            "bookingReference": "PI-123456789",
            "status": "%s"
          }
          """.formatted(status);
    }
  }

  private static String validResponseJson() {
    return """
        {
          "reservationByIdList": [
            {
              "reservationId": "RES-001",
              "rateInfo": {
                "summary": {
                  "totalCostOfStay": 89.00,
                  "currencyCode": "GBP",
                  "gross": 89.00,
                  "net": 74.17
                }
              },
              "reservationStatus": "RESERVED"
            }
          ],
          "bookingReference": "PI-123456789",
          "basketReference": "bsk-a1b2c3d4",
          "hotelId": "LONWAT",
          "currencyCode": "GBP",
          "totalCost": 89.00,
          "channel": "PI"
        }
        """;
  }
}
