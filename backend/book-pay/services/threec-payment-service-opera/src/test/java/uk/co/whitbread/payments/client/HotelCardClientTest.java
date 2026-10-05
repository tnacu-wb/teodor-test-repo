package uk.co.whitbread.payments.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import com.google.common.net.HttpHeaders;
import uk.co.whitbread.payments.model.card.PaymentCardDTO;
import uk.co.whitbread.payments.properties.HotelCardProperties;

@ExtendWith(MockitoExtension.class)
class HotelCardClientTest {

  private MockWebServer mockWebServer;
  private HotelCardClient hotelCardClient;
  @Mock
  private HotelCardProperties hotelCardProperties;

  @BeforeEach
  public void setup() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    var hotelCardWebClient = WebClient.create(String.format("http://localhost:%s",
        mockWebServer.getPort()));
    hotelCardClient = new HotelCardClient(hotelCardWebClient, hotelCardProperties);
  }

  @AfterEach
  public void tearDown() {
    mockWebServer.close();
  }

  @Test
  void verifySaveOrUpdateCardSuccess() {

    mockWebServer.enqueue(
        new MockResponse.Builder()
            .code(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    );
    var request = PaymentCardDTO.builder().build();

    var actualWebhookResponse = hotelCardClient.saveOrUpdateCard(request).block();
    assertNotNull(actualWebhookResponse);
  }

  @Test
  void verifySaveOrUpdateCardFailure() {

    var errorResponse = new MockResponse.Builder()
        .code(500)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body("error")
        .build();
    mockWebServer.enqueue(errorResponse);
    mockWebServer.enqueue(errorResponse);
    mockWebServer.enqueue(errorResponse);
    mockWebServer.enqueue(errorResponse);

    var request = PaymentCardDTO.builder().build();

     assertThrows(Exception.class, () -> hotelCardClient.saveOrUpdateCard(request).block());
  }
}
