package uk.co.whitbread.ohip.infrastructure.rest.client.frontdesk.ohip;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.frontdesk.ohip.properties.FrontDeskOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockOhiFrontDeskClientTest {

  @Mock
  private FrontDeskOhipProperties frontDeskOhipProperties;
  private WireMockServer wm;
  private String path = "http://localhost:8080";
  private WebClient webClient = WebClient.create(path);

  @BeforeEach
  void setUp() {
    wm = new WireMockServer(options().port(8080));

    wm.stubFor(get(path)
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipClient_ShouldReturnException() {
    final OhipFrontDeskClient ohipClient = new OhipFrontDeskClient(webClient, frontDeskOhipProperties);

    when(frontDeskOhipProperties.getCreditCardInfoEndpoint()).thenReturn(
        "/fof/config/v1/creditCardInfo");


    //Act
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getCreditCardInfo("hotelid", "cardId"));
  }
}
