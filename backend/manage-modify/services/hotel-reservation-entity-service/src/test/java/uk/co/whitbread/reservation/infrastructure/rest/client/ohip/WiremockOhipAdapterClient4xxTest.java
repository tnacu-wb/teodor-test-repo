package uk.co.whitbread.reservation.infrastructure.rest.client.ohip;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDetailsEnhancedDto;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationNotFoundException;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockOhipAdapterClient4xxTest {

  @Mock
  private OhipAdapterProperties ohipAdapterProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);
  private Body body = Body.fromJsonBytes(
      "{\"message\":\"message\", \"debugMessage\":\"message\", \"clause\":\"message\", \"errCode\":900}".getBytes());

  @BeforeEach
  void setUp() {
    wm = new WireMockServer(options().port(8080));

    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(body)));

    wm.stubFor(get(urlMatching("^.*Test.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(400)
            .withResponseBody(body)));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipAdapterClientReservationByBasketRef_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationEndpoint()).thenReturn(
        "/v1/reservations");
    //Act
    assertThrows(HotelReservationNotFoundException.class,
        () -> ohipAdapterClient.getReservationsByBasketReference("hotelId", "basketRef",
            1, 1));

  }

  @Test
  void testOhipAdapterClientReservationById_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationEndpoint()).thenReturn(
        "/v1/reservations");
    //Act
    assertThrows(HotelReservationNotFoundException.class,
        () -> ohipAdapterClient.sendGetReservationsByIds("hotelId", List.of("a", "b", "c"),
            true, false, true));
  }

  @Test
  void testOhipAdapterClientReservationByResId_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationIdEndpoint()).thenReturn(
        "/v1/reservation/reservationId");
    //Act
    assertThrows(HotelReservationNotFoundException.class,
        () -> ohipAdapterClient.sendGetReservationsByReservationId("resId", "hotelId"));
  }

  @Test
  void testOhipAdapterClientReservationByExternalRefId_ShouldReturnEmpty() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationIdEndpoint()).thenReturn(
        "/v1/reservation/reservationId");
    //Act
    var result = ohipAdapterClient.sendGetReservationsByExternalReferenceId("resId");
    assertNotNull(result);
    assertEquals(new ReservationDetailsEnhancedDto(), result);
  }

  @Test
  void testOhipAdapterClientReservationByExternalRefId_ShouldReturnEx() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationIdEndpoint()).thenReturn(
        "/v1/reservation/reservationId");
    //Act
    assertThrows(HotelReservationNotFoundException.class,
        () -> ohipAdapterClient.sendGetReservationsByExternalReferenceId("Test"));
  }
}
