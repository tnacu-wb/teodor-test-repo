package uk.co.whitbread.content.infrastructure.rest.client.ohip;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.InjectWireMock;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.OhipException;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.properties.OhipAdapterProperties;

@ExtendWith(MockitoExtension.class)
@ConfigureWireMock(name = "wmOhipServer", port = 8080)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockOhipClientTest {

  @Mock
  private OhipAdapterProperties properties;
  @InjectWireMock("wmOhipServer")
  WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final Body body = Body.fromJsonBytes(
      "{\"message\":\"message\", \"debugMessage\":\"message\", \"clause\":\"message\", \"errorCode\":900}".getBytes());


  @BeforeEach
  void setUp() {
    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));
  }

  @AfterEach
  void cleanUp() {
    wm.resetAll();
  }

  @Test
  void testAemClientHotelInfo_ShouldReturnException() {
    WebClient webClient = WebClient.create(path);
    OhipAdapterClient client = new OhipAdapterClient(properties, webClient);
    when(properties.getHotelInfoEndpoint()).thenReturn("/hotels/{hotelId}/info");

    //Act
    assertThrows(OhipException.class,
        () -> client.getHotelInfo("1"));

  }

  @Test
  void testAemClientRatePlans_ShouldReturnException() {
    WebClient webClient = WebClient.create(path);
    OhipAdapterClient client = new OhipAdapterClient(properties, webClient);
    when(properties.getRatePlansEndpoint()).thenReturn("/ratePlans");

    //Act
    assertThrows(OhipException.class,
        () -> client.sendGetRatePlansRequest(List.of("1"), "1"));

  }
}
