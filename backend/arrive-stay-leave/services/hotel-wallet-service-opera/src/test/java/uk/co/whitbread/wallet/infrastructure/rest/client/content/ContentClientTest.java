package uk.co.whitbread.wallet.infrastructure.rest.client.content;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.generated.models.content.HotelInformationDto;
import uk.co.whitbread.wallet.infrastructure.rest.client.CustomTestResponseSpec;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.service.ContentClient;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.service.exceptions.ContentException;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.service.properties.ContentProperties;

@ExtendWith(MockitoExtension.class)
class ContentClientTest {

  @InjectMocks
  private ContentClient contentClient;
  @Mock
  private WebClient contentWebClient;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;

  @Test
  void getHotelInformation__success() {

    when(contentWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInformationDto.class)).thenReturn(
        mockHotelInformationResponse());

    var response = contentClient.getHotelInformation("gb", "en", "FRAMTI");
    assertNotNull(response);
    MatcherAssert.assertThat(response, instanceOf(HotelInformationDto.class));
    verifyNoMoreInteractions(contentWebClient);
  }

  @Test
  void getHotelInformation_5xx() {
    when(contentWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);

    ContentException ex = mock(ContentException.class);
    when(responseSpecMock.bodyToMono(HotelInformationDto.class)).thenReturn(Mono.error(ex));
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(ContentException.class,
        () -> contentClient.getHotelInformation("gb", "en", "FRAMTI"));
  }

  @Test
  void getReservationDetailsWithURITest() throws IOException {
    ContentProperties contentProperties = new ContentProperties();
    contentProperties.setHost("host");
    contentProperties.setHotelInformationEndpoint("hotelInformation");

    MockWebServer mockWebServer = new MockWebServer();
    WebClient mockedWebClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build();
    contentClient = new ContentClient(mockedWebClient, contentProperties);
    assertNotNull(contentProperties.getHotelInformationEndpoint());
    mockWebServer.enqueue(
            new MockResponse().setResponseCode(HttpStatus.OK.value())
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(getMockedContentResponse())
    );

    HotelInformationDto result = contentClient.getHotelInformation("gb", "en", "FRAMTI");
    mockWebServer.close();
    assertNotNull(result);
    MatcherAssert.assertThat(result, instanceOf(HotelInformationDto.class));
    assertEquals("FRAMTI", result.getHotelId());
    assertEquals("testBrand", result.getBrand());
  }

  private static String getMockedContentResponse() {
    return "{\"brand\": \"testBrand\"," +
            "\"directions\": \"no\"," +
            "\"headline\": \"testHeadline\"," +
            "\"hotelDescription\": \"description\"," +
            "\"hotelId\": \"FRAMTI\"," +
            "\"hotelOpeningDate\": \"yes\"," +
            "\"name\": \"testName\"," +
            "\"parkingDescription\": \"description\"," +
            "\"satNavDirections\": \"directions\"," +
            "\"whatThreeWords\": \"words\"" +
            "}";
  }

  private Mono<HotelInformationDto> mockHotelInformationResponse() {
    return Mono.just(new HotelInformationDto());
  }
}
