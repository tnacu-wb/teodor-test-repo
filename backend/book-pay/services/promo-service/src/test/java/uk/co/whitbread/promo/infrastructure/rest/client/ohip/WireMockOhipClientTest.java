package uk.co.whitbread.promo.infrastructure.rest.client.ohip;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.promo.infrastructure.exception.OhipException;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.properties.OhipAdapterProperties;

@ExtendWith(MockitoExtension.class)
class WireMockOhipClientTest {

  @Mock
  private OhipAdapterProperties properties;
  private WireMockServer wm;
  private String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);


  @BeforeEach
  void setUp() {
    wm = new WireMockServer(options().port(8080));
    wm.stubFor(get(anyUrl())
            .willReturn(
                    aResponse()
                            .withStatus(500)
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .withBody("""
                  {
                    "errCode": 974,
                    "debugMessage": "internal.server.exception",
                    "globalErrTextTemplate": "The selected hotel is currently unavailable for promotion validation."
                  }
                  """)
            ));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipAdapterClientPromotions_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(properties,
        webClient);
    when(properties.getPromotionsEndpoint()).thenReturn(
        "/promotions");
    //Act
    List<String> promoCodes = List.of("FX10R");

    assertThrows(OhipException.class,
        () -> ohipAdapterClient.getPromotions(promoCodes, "AAAAA")
    );
  }
}


