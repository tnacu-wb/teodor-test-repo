package uk.co.whitbread.reservation.infrastructure.rest.client.cdh;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service.CdhAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.exceptions.CdhReservationException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockCdhAdapterClientTest {

  @Mock
  private CdhAdapterProperties cdhAdapterProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);
  private Body body = Body.fromJsonBytes(
          "{\"message\":\"message\", \"debugMessage\":\"message\", \"clause\":\"message\", \"errorCode\":900}".getBytes());

  @BeforeEach
  void setUp() {
    wm = new WireMockServer(options().port(8080));

    wm.stubFor(post(anyUrl())
            .willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withStatus(500)
                    .withResponseBody(body)));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testCdhAdapterClientIndexHeader_ShouldReturnException() {
    final CdhAdapterClient cdhAdapterClient = new CdhAdapterClient(cdhAdapterProperties, webClient);
    final CdhReservationSearchCriteriaDto cdhReservationSearchCriteriaDto = new CdhReservationSearchCriteriaDto();
    when(cdhAdapterProperties.getReservationSearchEndpoint()).thenReturn(
            "/v1/cdh/reservation/search");
    //Act
    assertThrows(CdhReservationException.class,
            () -> cdhAdapterClient.searchReservation(cdhReservationSearchCriteriaDto));
  }
}
