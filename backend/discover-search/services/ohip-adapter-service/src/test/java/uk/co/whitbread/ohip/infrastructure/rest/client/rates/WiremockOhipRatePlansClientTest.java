package uk.co.whitbread.ohip.infrastructure.rest.client.rates;

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
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.exceptions.RatePlansException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip.OhipRatePlansClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip.properties.RatePlansOhipProperties;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockOhipRatePlansClientTest {

  @Mock
  private RatePlansOhipProperties ratePlansOhipProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);

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
  void testOhipRatePlans_shouldReturnException() {
    final OhipRatePlansClient ohipRatePlansClient = new OhipRatePlansClient(webClient,
        ratePlansOhipProperties);
    when(ratePlansOhipProperties.getRatePlansEndpoint()).thenReturn(
        "/rtp/v1/ratePlans");

    //Act
    var response = ohipRatePlansClient.getRatePlans(List.of("testRatePlans"), "hotelId");
    assertThrows(RatePlansException.class, () -> response.block());
  }

  @Test
  void testOhipNegotiated_shouldReturnException() {
    final OhipRatePlansClient ohipRatePlansClient = new OhipRatePlansClient(webClient,
        ratePlansOhipProperties);
    when(ratePlansOhipProperties.getNegotiatedRatesByProfileIdEndpoint()).thenReturn(
        "/rtp/v1/profiles/{profileId}/negotiatedRates");

    //Act
    var response = ohipRatePlansClient.getNegotiatedRatesForProfileId("testProfileId");
    assertThrows(RatePlansException.class, () -> response.block());
  }


  @Test
  void testOhipRatePlansPromo_shouldReturnException() {
    final OhipRatePlansClient ohipRatePlansClient = new OhipRatePlansClient(webClient,
        ratePlansOhipProperties);
    when(ratePlansOhipProperties.getRatePlanPromoEndpoint()).thenReturn(
        "/rtp/v1/hotels/{hotelId}/ratePlans/{ratePlanCode}");

    //Act
    assertThrows(RatePlansException.class,
        () -> ohipRatePlansClient.getRatePlanInfo("testRatePlan", "hotelId"));

  }

  @Test
  void testOhipPromotionCode_shouldReturnException() {
    final OhipRatePlansClient ohipRatePlansClient = new OhipRatePlansClient(webClient,
        ratePlansOhipProperties);
    List<String> promotionCodes = List.of("FX10R");
    when(ratePlansOhipProperties.getPromotionCodeEndpoint()).thenReturn(
        "/rtp/v1/promotionCodes");

    //Act
    assertThrows(RatePlansException.class,
        () -> ohipRatePlansClient.getPromotionCode(promotionCodes, "HEAPTI"));
  }
}