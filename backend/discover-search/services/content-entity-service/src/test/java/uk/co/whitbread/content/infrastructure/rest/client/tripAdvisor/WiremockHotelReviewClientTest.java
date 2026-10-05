package uk.co.whitbread.content.infrastructure.rest.client.tripAdvisor;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
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
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.HotelReviewResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.HotelReviewProperties;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.HotelReviewClient;

@ExtendWith(MockitoExtension.class)
@ConfigureWireMock(name = "wmHotelReviewServer", port = 8080)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockHotelReviewClientTest {

  @Mock
  private HotelReviewProperties properties;
  @InjectWireMock("wmHotelReviewServer")
  WireMockServer wm;
  String path = "http://localhost:8080";

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
    lenient().when(properties.getReviewsForSingleHotelEndpoint()).thenReturn(
        "/v1/hotel-review/reviews/{hotelCode}");
    lenient().when(properties.getHost()).thenReturn("abcd");
    
    WebClient webClient = WebClient.create(path);
    HotelReviewClient client = new HotelReviewClient(properties, webClient);

    //Act
    assertThrows(HotelReviewResponseException.class,
        () -> client.getTripAdvisorReviews("code", "en"));
  }

}
