package uk.co.whitbread.content.infrastructure.rest.client.cookies;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
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
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.adapter.CookiePoliciesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.out.CookiePoliciesRequestAemDto;


@ExtendWith(MockitoExtension.class)
@ConfigureWireMock(name = "wmCookiePolitiesAemServer", port = 8080)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockCookiePolitiesAemClientTest {

  @Mock
  private AemProperties aemProperties;
  @InjectWireMock("wmCookiePolitiesAemServer")
  WireMockServer wm;
  String path = "http://localhost:8080";

  CookiePoliciesRequestAemDto request = CookiePoliciesRequestAemDto.builder().brand("1")
      .country("UK").language("de").build();

  @BeforeEach
  void setUp() {
    wm.stubFor(get("http://localhost:8080")
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));
  }

  @AfterEach
  void cleanUp() {
    wm.resetAll();
  }

  @Test
  void testAemClient_ShouldReturnException() {
    WebClient webClient = WebClient.create(path);
    CookiePoliciesAemClient aemClient = new CookiePoliciesAemClient(aemProperties, webClient);
    when(aemProperties.getCookiePoliciesEndpoint()).thenReturn(
        "/{country}/{language}/content-service.booking-flow.detail/bookingId/{bookingFlowId}.json");

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getCookiePolicies(request));
  }


}
