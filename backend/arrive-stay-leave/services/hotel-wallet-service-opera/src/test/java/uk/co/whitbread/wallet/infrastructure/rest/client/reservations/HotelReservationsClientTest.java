package uk.co.whitbread.wallet.infrastructure.rest.client.reservations;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.generated.models.reservation.FindBookingResponseDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationByBasketRefResponseDto;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.infrastructure.exceptions.HotelReservationException;
import uk.co.whitbread.wallet.infrastructure.rest.client.CustomTestResponseSpec;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.HotelReservationsClient;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.properties.HotelReservationsProperties;

@ExtendWith(MockitoExtension.class)
class HotelReservationsClientTest {

  @InjectMocks
  private HotelReservationsClient hotelReservationsClient;

  @Mock
  private WebClient reservationWebClient;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;

  @Test
  void getBasketReferenceTest() {
    var basketRequest = new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI", false);

    when(reservationWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(FindBookingResponseDto.class)).thenReturn(
        mockBookingDetailsResponse());

    var response = hotelReservationsClient.getBasketReference(basketRequest);
    assertNotNull(response);
    MatcherAssert.assertThat(response, instanceOf(FindBookingResponseDto.class));
    verifyNoMoreInteractions(reservationWebClient);
  }

  @Test
  void getReservationDetailsTest() {
    when(reservationWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationByBasketRefResponseDto.class)).thenReturn(
        mockReservationsDetailsResponse());

    var response = hotelReservationsClient.getReservationDetails("basketReference");
    assertNotNull(response);
    MatcherAssert.assertThat(response, instanceOf(ReservationByBasketRefResponseDto.class));
    verifyNoMoreInteractions(reservationWebClient);
  }

  @Test
  void getBasketReferenceTest_error_5xx() {
    var basketRequest = new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI", true);

    HotelReservationException ex = mock(HotelReservationException.class);
    when(customResponseSpec.bodyToMono(FindBookingResponseDto.class)).thenReturn(Mono.error(ex));
    when(reservationWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & assert
    assertThrows(HotelReservationException.class,
        () -> hotelReservationsClient.getBasketReference(basketRequest));
  }

  @Test
  void getReservationDetailsTest_error_5xx() {
    HotelReservationException ex = mock(HotelReservationException.class);
    when(customResponseSpec.bodyToMono(ReservationByBasketRefResponseDto.class)).thenReturn(
        Mono.error(ex));
    when(reservationWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & assert
    assertThrows(HotelReservationException.class,
        () -> hotelReservationsClient.getReservationDetails("basketReference"));
  }

  @Test
  void getReservationDetailsWithURITest() throws IOException {
    HotelReservationsProperties hotelReservationsProperties = new HotelReservationsProperties("basket","reservation","host");
      MockWebServer mockWebServer = new MockWebServer();
    WebClient mockedWebClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build();
    hotelReservationsClient = new HotelReservationsClient(mockedWebClient, hotelReservationsProperties);
    assertNotNull(hotelReservationsProperties.getReservationEndpoint());
    mockWebServer.enqueue(
            new MockResponse().setResponseCode(HttpStatus.OK.value())
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(getMockedReservationResponse())
    );

    ReservationByBasketRefResponseDto result = hotelReservationsClient.getReservationDetails("test");
    mockWebServer.close();
    assertNotNull(result);
    MatcherAssert.assertThat(result, instanceOf(ReservationByBasketRefResponseDto.class));
    assertEquals("PI", result.getChannel());
    assertEquals("testId", result.getHotelId());
  }

  @Test
  void getBasketReferenceWithURITest() throws IOException {
    var walletRequest = new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI", false);
    HotelReservationsProperties hotelReservationsProperties = new HotelReservationsProperties("basket","reservation","host");

    MockWebServer mockWebServer = new MockWebServer();
    WebClient mockedWebClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build();
    hotelReservationsClient = new HotelReservationsClient(mockedWebClient, hotelReservationsProperties);
    assertNotNull(hotelReservationsProperties.getBasketReferenceEndpoint());
    mockWebServer.enqueue(
            new MockResponse().setResponseCode(HttpStatus.OK.value())
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(getMockedBasketResponse())
    );

    FindBookingResponseDto result = hotelReservationsClient.getBasketReference(walletRequest);
    mockWebServer.close();
    MatcherAssert.assertThat(result, instanceOf(FindBookingResponseDto.class));
    assertEquals("120", result.getBasketReference());
  }

  private Mono<FindBookingResponseDto> mockBookingDetailsResponse() {
    return Mono.just(new FindBookingResponseDto());
  }

  private Mono<ReservationByBasketRefResponseDto> mockReservationsDetailsResponse() {
    return Mono.just(new ReservationByBasketRefResponseDto());
  }

  private static String getMockedReservationResponse() {
    return "{\"balanceOutstanding\": \"120\"," +
            "\"basketReference\": \"test\"," +
            "\"bookingReference\": \"test\"," +
            "\"channel\": \"PI\"," +
            "\"companyId\": \"testId\"," +
            "\"currencyCode\": \"code\"," +
            "\"customReferenceNumber\": \"reference\"," +
            "\"discount\": \"0\"," +
            "\"hasCityTax\": \"false\"," +
            "\"hotelId\": \"testId\"," +
            "\"isCnp\": \"false\"," +
            "\"newTotal\": \"0\"," +
            "\"policyCode\": \"policy\"," +
            "\"previousTotal\":\" 0\"," +
            "\"purchaseOrderNumber\": \"1\"," +
            "\"reservationByIdList\": []," +
            "\"totalCost\": \"0\"," +
            "\"totalCostWoDiscount\": \"0\"" +
            "}";
  }

  private static String getMockedBasketResponse() {
    return "{\"basketReference\": \"120\"," +
            "\"cookieName\": \"test\"," +
            "\"minutesTillExpiry\": \"10\"," +
            "\"redirectBase\": \"base\"," +
            "\"ref\": \"testId\"," +
            "\"sourcePms\": \"source\"," +
            "\"token\": \"testToken\"" +
            "}";
  }
}
