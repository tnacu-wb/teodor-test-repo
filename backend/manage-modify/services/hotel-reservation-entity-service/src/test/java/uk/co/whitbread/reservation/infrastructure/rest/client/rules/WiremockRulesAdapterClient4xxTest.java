package uk.co.whitbread.reservation.infrastructure.rest.client.rules;

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
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.exceptions.RulesException;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.RulesAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.properties.RulesAdapterProperties;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockRulesAdapterClient4xxTest {
  @Mock
  private RulesAdapterProperties rulesAdapterProperties;
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
  void testRulesAdapterClientMaxRoomOccupancy_ShouldReturnException() {
    final RulesAdapterClient rulesAdapterClient = new RulesAdapterClient(webClient, rulesAdapterProperties);
    when(rulesAdapterProperties.getMaxRoomOccupancyEndpoint()).thenReturn(
            "/v1/rules/max-room-occupancy");
    //Act
    assertThrows(RulesException.class,
            () -> rulesAdapterClient.getMaxRoomOccupancyResponse("channelId", "brand"));
  }

  @Test
  void testRulesAdapterClientGetChannelBasedOnSourceId_ShouldReturnException() {
    final RulesAdapterClient rulesAdapterClient = new RulesAdapterClient(webClient, rulesAdapterProperties);
    when(rulesAdapterProperties.getChannelBasedOnSourceIdEndpoint()).thenReturn(
            "/v1/rules/source-info");
    //Act
    assertThrows(RulesException.class,
            () -> rulesAdapterClient.getChannelBasedOnSourceId("sourceId"));
  }

  @Test
  void testRulesAdapterClientVatCodesForPackages_ShouldReturnException() {
    final RulesAdapterClient rulesAdapterClient = new RulesAdapterClient(webClient, rulesAdapterProperties);
    when(rulesAdapterProperties.getAmendmentRulesEndpoint()).thenReturn(
            "/v1/rules/vat-codes");
    //Act
    assertThrows(RulesException.class,
            () -> rulesAdapterClient.getVatCodesForPackage("vatRegion", "packageCode"));
  }

}
