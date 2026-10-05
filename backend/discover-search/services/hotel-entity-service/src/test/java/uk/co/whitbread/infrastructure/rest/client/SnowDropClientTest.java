package uk.co.whitbread.infrastructure.rest.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import uk.co.whitbread.infrastructure.rest.client.distance.exception.SnowDropException;
import uk.co.whitbread.infrastructure.rest.client.distance.model.in.HotelLocationRequest;
import uk.co.whitbread.infrastructure.rest.client.distance.model.out.HotelLocationResponse;
import uk.co.whitbread.infrastructure.rest.client.distance.model.out.MapLocation;
import uk.co.whitbread.infrastructure.rest.client.hotelsearch.exception.HotelSearchLocationException;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class SnowDropClientTest {
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private CustomTestResponseSpec responseSpec;
  @InjectMocks
  private SnowDropClient snowDropWebClient;

  @Test
  void getHotelsLocationByLatLong__shouldReturnOk() {

    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(HotelLocationResponse.class)).thenReturn(mockHotelLocationListResponse());

    //Act
    List<String> latlongLocation = List.of("51.533674", "-0.122153");
    var result = snowDropWebClient.getHotelsLocationByLatLong(createHotelLocationRequestByLatLong(),
        latlongLocation);

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.get(0).getCode(), is("LONLEI"));
    assertThat(result.get(0).getLocation().getLatitude(), is(51.511143));
    assertThat(result.get(0).getLocation().getLongitude(), is(-0.13035));
    assertThat(result.get(0).getName(), is("London Leicester Square"));
    assertThat(result.get(0).getBrand(), is("PI"));
    assertThat(result.get(0).getDistance(), is("754"));
  }

  @Test
  void getHotelsLocationByLatLong__shouldThrowSnowDropExceptionOn5xx() {
    //Arrange
    List<String> latlongLocation = List.of("51.533674", "-0.122153");
    var distance = createHotelLocationRequestByLatLong();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    var exception = Assertions.assertThrows(SnowDropException.class,
        () -> snowDropWebClient.getHotelsLocationByLatLong(distance, latlongLocation));

    // Assert
    Assertions.assertEquals("SnowDrop server error", exception.getDebugMessage());
  }

  @Test
  void getHotelsLocationByLatLong__shouldThrowHotelSearchLocationExceptionOn4xx() {
    //Arrange
    List<String> latlongLocation = List.of("51.533674", "-0.122153");
    var distance = createHotelLocationRequestByLatLong();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    var exception = Assertions.assertThrows(HotelSearchLocationException.class,
        () -> snowDropWebClient.getHotelsLocationByLatLong(distance, latlongLocation));

    // Assert
    Assertions.assertEquals("Resource not found in SnowDrop", exception.getDebugMessage());
  }

  @Test
  void getHotelsLocationByPlaceId__shouldReturnOk() {

    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(HotelLocationResponse.class)).thenReturn(mockHotelLocationListResponse());

    //Act
    var result = snowDropWebClient.getHotelsLocationByPlaceId(createHotelLocationRequestByPlaceId(),
        "placeId");

    assertThat(result, notNullValue());
    assertThat(result.get(0).getCode(), is("LONLEI"));
    assertThat(result.get(0).getLocation().getLatitude(), is(51.511143));
    assertThat(result.get(0).getLocation().getLongitude(), is(-0.13035));
    assertThat(result.get(0).getName(), is("London Leicester Square"));
    assertThat(result.get(0).getBrand(), is("PI"));
    assertThat(result.get(0).getDistance(), is("754"));
  }

  @Test
  void getHotelsLocationByPlaceId__shouldThrowSnowDropExceptionOn5xx() {

    //Arrange
    String locationFormat = "placeId";
    var distance = createHotelLocationRequestByPlaceId();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    var exception = Assertions.assertThrows(SnowDropException.class,
            () -> snowDropWebClient.getHotelsLocationByPlaceId(distance, locationFormat));

    // Assert
    Assertions.assertEquals("SnowDrop server error", exception.getDebugMessage());
  }

  @Test
  void getHotelsLocationByPlaceId__shouldThrowHotelSearchLocationExceptionOn4xx() {

    //Arrange
    String locationFormat = "placeId";
    var distance = createHotelLocationRequestByPlaceId();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    var exception = Assertions.assertThrows(HotelSearchLocationException.class,
            () -> snowDropWebClient.getHotelsLocationByPlaceId(distance, locationFormat));

    // Assert
    Assertions.assertEquals("Resource not found in SnowDrop", exception.getDebugMessage());
  }

  private HotelLocationRequest createHotelLocationRequestByLatLong() {
    return HotelLocationRequest
        .builder()
        .hotelId("LONLEI")
        .location("51.511143,-0.13035")
        .locationFormat("latlong")
        .build();
  }

  private HotelLocationRequest createHotelLocationRequestByPlaceId() {
    return HotelLocationRequest
        .builder()
        .hotelId("LONLEI")
        .location("ChIJdd4hrwug2EcRmSrV3Vo6llI")
        .locationFormat("placeId")
        .build();
  }

  private Flux<HotelLocationResponse> mockHotelLocationListResponse() {

    var location = MapLocation.builder()
        .latitude(51.511143)
        .longitude(-0.13035)
        .build();

    var hotelLocationResponse1 = HotelLocationResponse.builder()
        .code("LONLEI")
        .distance("754")
        .name("London Leicester Square")
        .brand("PI")
        .location(location)
        .build();

    return Flux.just(hotelLocationResponse1);
  }
}
