package uk.co.whitbread.ohip.infrastructure.rest.client.rules;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
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
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.exceptions.BookingChannelException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.exceptions.RoomSubstitutionException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.properties.RulesAgentProperties;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WiremockRulesAgentClientTest {

  @Mock
  private RulesAgentProperties rulesAgentProperties;
  private WireMockServer wm;
  private String path;
  private WebClient webClient;
  private Body body = Body.fromJsonBytes(
      "{\"message\":\"message\", \"debugMessage\":\"message\", \"clause\":\"message\", \"errorCode\":900}".getBytes());


  @BeforeAll
  void startWiremock() {
    wm = new WireMockServer(options().dynamicPort());
    wm.start();
    path = wm.baseUrl();
    webClient = WebClient.create(path);
  }

  @BeforeEach
  void setUp() {
    when(rulesAgentProperties.getVatCodesEndpoint()).thenReturn(
        "/vat-codes");
    when(rulesAgentProperties.getRoomSubstitutionEndpoint()).thenReturn(
        "/room-substitutions");
    when(rulesAgentProperties.getBookingChannelInfo()).thenReturn(
        "/channel-info");
    when(rulesAgentProperties.getBusinessAllowanceEndpoint()).thenReturn(
        "/allowances");
    when(rulesAgentProperties.getChannelSourceInfoEndpoint()).thenReturn(
        "/source-info");

    wm.resetAll();

    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));

  }

  @AfterAll
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testRulesAgentRoomSubstitution_shouldReturnException() {
    final RulesAgentClient rulesAgentClient = new RulesAgentClient(webClient, rulesAgentProperties);
    //Act
    assertThrows(RoomSubstitutionException.class,
        () -> rulesAgentClient.getRoomSubstitution("testRoomType", 2, 1,
            "testChannel"));
  }

  @Test
  void testRulesAgentBookingChannelInfo_shouldReturnException() {
    final RulesAgentClient rulesAgentClient = new RulesAgentClient(webClient, rulesAgentProperties);
    //Act
    final BookingChannel request = BookingChannel.builder().channel("PI").subchannel("WEB")
        .language("EN").build();
    assertThrows(BookingChannelException.class,
        () -> rulesAgentClient.getBookingChannelInfo(request));
  }

  @Test
  void testRulesAgentVatCodes_shouldReturnException() {
    final RulesAgentClient rulesAgentClient = new RulesAgentClient(webClient, rulesAgentProperties);
    final List<String> list = List.of("pkgCodeArr");
    //Act
    assertThrows(BookingChannelException.class,
        () -> rulesAgentClient.getVatCodes("testVatRegion", list));
  }

  @Test
  void testRulesAgentAllowances_shouldReturnException() {
    final RulesAgentClient rulesAgentClient = new RulesAgentClient(webClient, rulesAgentProperties);
    //Act
    assertThrows(BookingChannelException.class, rulesAgentClient::getBusinessAllowances);
  }

  @Test
  void testRulesAgentChannelSourceInfo_shouldReturnException() {
    final RulesAgentClient rulesAgentClient = new RulesAgentClient(webClient, rulesAgentProperties);
    //Act
    assertThrows(BookingChannelException.class,
        () -> rulesAgentClient.getChannelSourceInfo("testSourceId"));
  }
}
