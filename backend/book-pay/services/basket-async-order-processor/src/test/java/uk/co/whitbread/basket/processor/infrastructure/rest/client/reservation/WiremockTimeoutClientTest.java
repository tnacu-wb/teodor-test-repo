package uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.lang.Math.toIntExact;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;

import java.time.Duration;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.AmendReservationException;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.HotelReservationException;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation.properties.ReservationsClientProperties;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.hotel.reservation.generated.models.CancelReservationRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmAmendRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmReservationRequestDto;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WiremockTimeoutClientTest {

  @Mock
  private ReservationsClientProperties reservationsClientProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);
  private Body body = Body.fromJsonBytes(
      "{\"message\":\"message\", \"debugMessage\":\"debugMessage\", \"clause\":\"clause\", \"errCode\":900}".getBytes());

  private final String timeoutMessage = "java.net.http.HttpTimeoutException: ReadTimeout";

  private final ConfirmAmendRequestDto amendRequest = new ConfirmAmendRequestDto();
  private final ConfirmReservationRequestDto reservationRequest = new ConfirmReservationRequestDto();
  private final CancelReservationRequestDto cancelRequest = new CancelReservationRequestDto();

  @BeforeEach
  void setUp() {
    when(reservationsClientProperties.getAmendReservationEndpoint()).thenReturn(
        "/v1/reservations/confirm");
    when(reservationsClientProperties.getConfirmReservationEndpoint()).thenReturn(
        "/v1/reservations/amend/confirmAmend");
    when(reservationsClientProperties.getCancelReservationEndpoint()).thenReturn(
        "/v1/reservations/cancellations/rollback");
    wm = new WireMockServer(options().port(8080));
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testConfirmAmend_ShouldReturnTimeoutException() {
    //Arrange
    stubTimeoutException();
    ReservationsClient client = new ReservationsClient(webClient, reservationsClientProperties);
    ReflectionTestUtils.setField(client, "duration", Duration.ofSeconds(1));
    //Act & assert
    var ex = assertThrows(Exception.class,
        () -> client.confirmAmend(amendRequest));
    assertEquals("java.net.http.HttpTimeoutException: ReadTimeout", ex.getMessage());
  }

  @Test
  void testConfirmReservation_ShouldReturnTimeoutException() {
    //Arrange
    stubTimeoutException();
    ReservationsClient client = new ReservationsClient(webClient, reservationsClientProperties);
    ReflectionTestUtils.setField(client, "duration", Duration.ofSeconds(1));
    //Act & assert
    var ex = assertThrows(Exception.class,
        () -> client.confirmReservation(reservationRequest));
    assertEquals("java.net.http.HttpTimeoutException: ReadTimeout", ex.getMessage());
  }

  @Test
  void testCancelReservation_ShouldReturnTimeoutException() {
    //Arrange
    stubTimeoutException();
    ReservationsClient client = new ReservationsClient(webClient, reservationsClientProperties);
    ReflectionTestUtils.setField(client, "duration", Duration.ofSeconds(1));
    //Act & assert
    var ex = assertThrows(Exception.class,
        () -> client.cancelReservation(cancelRequest));
    assertEquals("java.net.http.HttpTimeoutException: ReadTimeout", ex.getMessage());
  }

  @Test
  void testConfirmAmendReservation_ShouldReturnAmendReservationException() {
    //Arrange
    stubHotelException();
    ReservationsClient client = new ReservationsClient(webClient, reservationsClientProperties);
    //Act
    var ex = assertThrows(AmendReservationException.class,
        () -> client.confirmAmend(amendRequest));
    assertHotelReservationException(ex);
  }

  @Test
  void testConfirmReservation_ShouldReturnHotelException() {
    //Arrange
    stubHotelException();
    ReservationsClient client = new ReservationsClient(webClient, reservationsClientProperties);
    //Act & assert
    var ex = assertThrows(HotelReservationException.class,
        () -> client.confirmReservation(reservationRequest));
    assertHotelReservationException(ex);
  }

  @Test
  void testCancelReservation_ShouldReturnHotelException() {
    //Arrange
    stubHotelException();
    ReservationsClient client = new ReservationsClient(webClient, reservationsClientProperties);
    //Act & assert
    var ex = assertThrows(HotelReservationException.class,
        () -> client.cancelReservation(cancelRequest));
    assertHotelReservationException(ex);
  }

  private void stubHotelException() {
    wm.stubFor(any(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));
    wm.start();
  }

  private void stubTimeoutException() {
    wm.stubFor(any(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withFixedDelay(toIntExact(Duration.ofSeconds(2).toMillis()))
            .withBody(timeoutMessage)));
    wm.start();
  }

  private static void assertHotelReservationException(AbstractInternalException ex) {
    assertEquals("message", ex.getGlobalErrTextTemplate());
    assertEquals("debugMessage", ex.getDebugMessage());
    assertEquals(900, ex.getErrorCode());
  }

}
