package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.ohip;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.exceptions.EckohException;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.ohip.properties.EckohOhiPproperties;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WiremockEckohOhipClientTest {

  @Mock
  private EckohOhiPproperties eckohOhiPproperties;
  private WireMockServer wm;
  private String path;
  private WebClient webClient;
  private EckohOhipClient ohipClient;

  @BeforeAll
  void startWiremock() {
    wm = new WireMockServer(options().dynamicPort());
    wm.start();
    path = wm.baseUrl();
    webClient = WebClient.create(path);
  }

  @BeforeEach
  void setUp() {
    wm.resetAll();

    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));

    wm.stubFor(post(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withBody("exception")));
  }

  @AfterAll
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipClient_getReservationsByBlockId_ShouldReturnException() {
    ohipClient = new EckohOhipClient(webClient, eckohOhiPproperties);
    when(eckohOhiPproperties.getExternalReservationEndpoint()).thenReturn("/rsv/v1/reservations");

    //Act
    assertThrows(EckohException.class,
        () -> ohipClient.getReservationsByBlockId("blockId"));
  }

  @Test
  void testOhipClient_sendChangeReservationRequest_ShouldReturnException() {
    ohipClient = new EckohOhipClient(webClient, eckohOhiPproperties);
    final ChangeReservation req = new ChangeReservation();
    when(eckohOhiPproperties.getReservationEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations");

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/res"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    //Act
    assertThrows(EckohException.class,
        () -> ohipClient.sendChangeReservationRequest("hotelId", "res", req));
  }

  @Test
  void testOhipClient_sendChangeReservationRequest_ShouldRetryCall() {
    ohipClient = new EckohOhipClient(webClient, eckohOhiPproperties);
    final ChangeReservation req = new ChangeReservation();
    when(eckohOhiPproperties.getReservationEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations");

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/res"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}"))
        .willSetStateTo("Second Call"));

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/res"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Second Call")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"status\": \"Success\"}")));

    var response = ohipClient.sendChangeReservationRequest("hotelId", "res", req);

    // Assert
    assertNotNull(response);
    assertInstanceOf(ChangeReservationDetails.class, response);

    // Verify retry logic
    wm.verify(2, putRequestedFor(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/res")));
  }

  @Test
  void testOhipClient_sendChangeReservationRequest_ShouldReturnException_WhenRetriesExhausted() {
    ohipClient = new EckohOhipClient(webClient, eckohOhiPproperties);
    final ChangeReservation req = new ChangeReservation();
    when(eckohOhiPproperties.getReservationEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations");

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/res"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    // Assert
    assertThrows(EckohException.class,
        () -> ohipClient.sendChangeReservationRequest("hotelId", "res", req));

    // Verify retry logic
    wm.verify(4, putRequestedFor(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/res")));
  }
}
