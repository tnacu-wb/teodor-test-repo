package uk.co.whitbread.reservation.infrastructure.rest.client.content.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.NoHeaderDataException;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.NoSearchRulesDataException;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.service.properties.ContentProperties;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockBasketContent4xxTest {

  @Mock
  private ContentProperties properties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);
  private Body body = Body.fromJsonBytes(
      "{\"message\":\"message\", \"debugMessage\":\"message\", \"clause\":\"message\", \"errorCode\":900}".getBytes());

  @BeforeEach
  void setUp() {
    wm = new WireMockServer(options().port(8080));

    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(body)));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testContentClientIndexHeader_ShouldReturnException() {
    final ContentClient contentClient = new ContentClient(webClient, properties);
    when(properties.getContentIndexHeaderEndpoint()).thenReturn(
            "/v1/content/header/data");
    //Act
    assertThrows(NoHeaderDataException.class,
            () -> contentClient.getIndexHeaderData("country", "language"));
  }

  @Test
  void testHotelPaymentInformation_ShouldReturnException() {
    final ContentClient client = new ContentClient(webClient, properties);
    when(properties.getHotelPaymentInformation()).thenReturn(
        "/v1/content/hotels/{hotelId}/payment-information");
    //Act
    assertThrows(NoHeaderDataException.class,
        () -> client.getHotelPaymentInformation("hotelid", "en", "EN"));
  }

  @Test
  void testContentClientHotelInformation_ShouldReturnException() {
    final ContentClient client = new ContentClient(webClient, properties);
    when(properties.getHotelInformationEndpoint()).thenReturn(
            "/v1/content/hotels/{hotelId}/information");
    //Act
    assertThrows(NoHeaderDataException.class,
            () -> client.getHotelInformation("hotelid", "en", "EN"));
  }

  @Test
  void testContentClientHotelRateInformation_ShouldReturnException() {
    final ContentClient client = new ContentClient(webClient, properties);
    when(properties.getHotelRateInformationEndpoint()).thenReturn(
            "/v1/content/booking/hotelRateInformation");
    //Act
    assertThrows(NoHeaderDataException.class,
            () -> client.getHotelRateInformation("hotelid", "en", "EN"));
  }

  @Test
  void testContentClientSearchRules_ShouldReturnException() {
    final ContentClient client = new ContentClient(webClient, properties);
    when(properties.getSearchRulesEndpoint()).thenReturn(
        "/v1/content/searchrules");
    //Act
    assertThrows(NoSearchRulesDataException.class,
        () -> client.getSearchRules("DISTR", Optional.of("PI")));
  }

}
