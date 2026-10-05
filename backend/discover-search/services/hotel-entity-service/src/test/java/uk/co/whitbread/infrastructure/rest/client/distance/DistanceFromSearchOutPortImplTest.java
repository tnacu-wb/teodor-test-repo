package uk.co.whitbread.infrastructure.rest.client.distance;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.distance.in.DistanceFromSearchRequest;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.model.distance.out.HotelLocation;
import uk.co.whitbread.infrastructure.rest.client.SnowDropClient;
import uk.co.whitbread.infrastructure.rest.client.distance.exception.HotelDistanceException;
import uk.co.whitbread.infrastructure.rest.client.distance.exception.SnowDropException;
import uk.co.whitbread.infrastructure.rest.client.distance.mapper.HotelLocationRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.distance.mapper.HotelLocationResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.distance.model.in.HotelLocationRequest;
import uk.co.whitbread.infrastructure.rest.client.distance.model.out.HotelLocationResponse;
import uk.co.whitbread.infrastructure.rest.client.distance.model.out.MapLocation;

@ExtendWith(MockitoExtension.class)
class DistanceFromSearchOutPortImplTest {

  @InjectMocks
  private DistanceFromSearchOutPortImpl distanceFromSearchOutPort;
  @Mock
  private SnowDropClient snowDropClient;
  @Mock
  private HotelLocationRequestMapper hotelLocationRequestMapper;
  @Mock
  private HotelLocationResponseMapper hotelLocationResponseMapper;

  @Test
  void getDistanceFromSearchByPlaceId__ShouldReturnOK() {
    // Arrange
    when(hotelLocationRequestMapper.toDto(any())).thenReturn(mockHotelLocationRequestByPlaceId());
    when(snowDropClient.getHotelsLocationByPlaceId(
        any(), any())).thenReturn(Collections.singletonList(mockHotelLocationResponse()));
    when(hotelLocationResponseMapper.toModel(any())).thenReturn(mockDistanceFromSearchResponse());

    //Act
    var distanceFromSearchResponse = distanceFromSearchOutPort.getDistanceFromSearch(
        mockDistanceFromSearchRequestByPlaceId());

    //Assert
    assertThat(distanceFromSearchResponse, notNullValue());
  }

  @Test
  void getDistanceFromSearchByLatLong__ShouldReturnOK() {
    // Arrange
    when(hotelLocationRequestMapper.toDto(any())).thenReturn(mockHotelLocationRequestByLatLong());
    when(snowDropClient.getHotelsLocationByLatLong(
        any(), any())).thenReturn(Collections.singletonList(mockHotelLocationResponse()));
    when(hotelLocationResponseMapper.toModel(any())).thenReturn(mockDistanceFromSearchResponse());

    //Act
    var distanceFromSearchResponse = distanceFromSearchOutPort.getDistanceFromSearch(
        mockDistanceFromSearchRequestByLatLong());

    //Assert
    assertThat(distanceFromSearchResponse, notNullValue());
  }

  @Test
  void getDistanceFromSearchByPlaceId__ShouldReturnException() {
    String expectedMessage = "Hotel with location was not found";
    // Arrange
    when(hotelLocationRequestMapper.toDto(any())).thenReturn(mockHotelLocationRequestByPlaceId());
    when(snowDropClient.getHotelsLocationByPlaceId(
        any(), any(String.class))).thenThrow(
        new SnowDropException("message", "Hotel with location was not found", new Exception(), 1));

    DistanceFromSearchRequest distanceFromSearchRequest = DistanceFromSearchRequest.builder()
        .hotelId("x")
        .location("X")
        .locationFormat("x")
        .radius(0)
        .radiusUnit("x")
        .build();
    //Act
    Exception exception = assertThrows(SnowDropException.class, () ->
        distanceFromSearchOutPort.getDistanceFromSearch(distanceFromSearchRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
  }

  @Test
  void getDistanceFromSearchByManagedPlaceId__ShouldReturnException() {
    String expectedMessage = "Resource not found in SnowDrop";

    // Arrange
    when(hotelLocationRequestMapper.toDto(any())).thenReturn(
        mockHotelLocationRequestByManagedPlaceId());
    when(snowDropClient.getHotelsLocationByPlaceId(
        any(), any(String.class))).thenThrow(
        new SnowDropException("message", "Resource not found in SnowDrop", new Exception(), 1));
    DistanceFromSearchRequest distanceFromSearchRequest = DistanceFromSearchRequest.builder()
        .hotelId("x")
        .location("X")
        .locationFormat("managePlaceId")
        .radius(0)
        .radiusUnit("x")
        .build();

    //Act
    Exception exception = assertThrows(SnowDropException.class, () ->
        distanceFromSearchOutPort.getDistanceFromSearch(distanceFromSearchRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
  }

  @Test
  void getHotelDistancesFromSearchByPlaceId__ShouldReturnOK() {
    // Arrange
    when(hotelLocationRequestMapper.toDto(any())).thenReturn(mockHotelLocationRequestByPlaceId());
    when(snowDropClient.getHotelsLocationByPlaceId(
        any(), any())).thenReturn(mockHotelLocationListResponse());
    when(hotelLocationResponseMapper.toModels(any())).thenReturn(
        mockDistanceFromSearchListResponse());

    //Act
    var distanceFromSearchResponse = distanceFromSearchOutPort.getHotelDistancesFromSearch(
        mockDistanceFromSearchRequestByPlaceId());

    //Assert
    assertThat(distanceFromSearchResponse, notNullValue());
    MatcherAssert.assertThat(distanceFromSearchResponse, hasSize(2));

    var hotelLocation = distanceFromSearchResponse.get(0);
    assertThat(hotelLocation, notNullValue());

    MatcherAssert.assertThat(hotelLocation.getName(), is("London Leicester Square"));
    MatcherAssert.assertThat(hotelLocation.getHotelId(), is("LONLEI"));
    MatcherAssert.assertThat(hotelLocation.getDistance(), is("754"));
    MatcherAssert.assertThat(hotelLocation.getBrand(), is("PI"));
    MatcherAssert.assertThat(hotelLocation.getLocation(), notNullValue());
  }

  @Test
  void getHotelDistancesFromSearchByLatLong__ShouldReturnOK() {
    // Arrange
    when(hotelLocationRequestMapper.toDto(any())).thenReturn(mockHotelLocationRequestByLatLong());
    when(snowDropClient.getHotelsLocationByLatLong(
        any(), any())).thenReturn(mockHotelLocationListResponse());
    when(hotelLocationResponseMapper.toModels(any())).thenReturn(
        mockDistanceFromSearchListResponse());

    //Act
    var distanceFromSearchResponse = distanceFromSearchOutPort.getHotelDistancesFromSearch(
        mockDistanceFromSearchRequestByLatLong());

    //Assert
    assertThat(distanceFromSearchResponse, notNullValue());
    MatcherAssert.assertThat(distanceFromSearchResponse, hasSize(2));

    var hotelLocation = distanceFromSearchResponse.get(0);
    assertThat(hotelLocation, notNullValue());

    MatcherAssert.assertThat(hotelLocation.getName(), is("London Leicester Square"));
    MatcherAssert.assertThat(hotelLocation.getHotelId(), is("LONLEI"));
    MatcherAssert.assertThat(hotelLocation.getDistance(), is("754"));
    MatcherAssert.assertThat(hotelLocation.getBrand(), is("PI"));
    MatcherAssert.assertThat(hotelLocation.getLocation(), notNullValue());
  }

  @Test
  void getDistanceFromSearchInvalidLocation__ShouldReturnException() {
    // Arrange
    when(hotelLocationRequestMapper.toDto(any())).thenReturn(
        mockHotelLocationRequestByInvalidLatLong());

    DistanceFromSearchRequest distanceFromSearchRequest = DistanceFromSearchRequest
        .builder()
        .hotelId("LONEUS")
        .location("51.533674,-0.122153,-0.122153")
        .locationFormat("latlong")
        .build();

    String expectedMessage = String.format("Error while trying to get lat long location for location=%s",
            distanceFromSearchRequest.getLocation());
    //Act
    Exception exception = assertThrows(HotelDistanceException.class, () ->
        distanceFromSearchOutPort.getDistanceFromSearch(distanceFromSearchRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
  }

  @Test
  void getDistanceFromSearch__ShouldReturnException() {
    String expectedMessage = "Hotel with location was not found!";
    // Arrange
    when(hotelLocationRequestMapper.toDto(any())).thenReturn(mockHotelLocationRequestByPlaceId());

    DistanceFromSearchRequest distanceFromSearchRequest = DistanceFromSearchRequest.builder()
        .hotelId("x")
        .location("X")
        .locationFormat("x")
        .radius(0)
        .radiusUnit("x")
        .build();
    //Act
    Exception exception = assertThrows(SnowDropException.class, () ->
        distanceFromSearchOutPort.getDistanceFromSearch(distanceFromSearchRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
  }

  private DistanceFromSearchResponse mockDistanceFromSearchResponse() {
    return DistanceFromSearchResponse
        .builder()
        .hotelId("LONEUS")
        .distance("1234")
        .build();
  }

  private HotelLocationResponse mockHotelLocationResponse() {
    return HotelLocationResponse.builder()
        .code("LONEUS")
        .distance("1234")
        .build();
  }

  private HotelLocationRequest mockHotelLocationRequestByPlaceId() {
    return HotelLocationRequest
        .builder()
        .hotelId("LONEUS")
        .location("ChIJdd4hrwug2EcRmSrV3Vo6llI")
        .locationFormat("placeId")
        .build();
  }

  private HotelLocationRequest mockHotelLocationRequestByManagedPlaceId() {
    return HotelLocationRequest
        .builder()
        .hotelId("LONEUS")
        .location("ChIJdd4hrwug2EcRmSrV3Vo6llI")
        .locationFormat("managedPlaceId")
        .build();
  }

  private HotelLocationRequest mockHotelLocationRequestByLatLong() {
    return HotelLocationRequest
        .builder()
        .hotelId("LONEUS")
        .location("51.533674,-0.122153")
        .locationFormat("latlong")
        .build();
  }

  private HotelLocationRequest mockHotelLocationRequestByInvalidLatLong() {
    return HotelLocationRequest
        .builder()
        .hotelId("LONEUS")
        .location("51.533674,-0.122153,-0.122153")
        .locationFormat("latlong")
        .build();
  }

  private DistanceFromSearchRequest mockDistanceFromSearchRequestByLatLong() {
    return DistanceFromSearchRequest
        .builder()
        .hotelId("LONEUS")
        .location("51.533674,-0.122153")
        .locationFormat("latlong")
        .build();
  }

  private DistanceFromSearchRequest mockDistanceFromSearchRequestByPlaceId() {
    return DistanceFromSearchRequest
        .builder()
        .hotelId("LONEUS")
        .location("ChIJdd4hrwug2EcRmSrV3Vo6llI")
        .locationFormat("placeId")
        .build();
  }

  private List<HotelLocationResponse> mockHotelLocationListResponse() {

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

    var hotelLocationResponse2 = HotelLocationResponse.builder()
        .code("LONSTM")
        .distance("906")
        .name("hub London Covent Garden")
        .brand("HUB")
        .location(location)
        .build();

    return List.of(hotelLocationResponse1, hotelLocationResponse2);
  }

  private List<DistanceFromSearchResponse> mockDistanceFromSearchListResponse() {

    var location = HotelLocation.builder()
        .latitude(51.511143)
        .longitude(-0.13035)
        .build();

    var distanceFromSearchResponse1 = DistanceFromSearchResponse.builder()
        .hotelId("LONLEI")
        .distance("754")
        .name("London Leicester Square")
        .brand("PI")
        .location(location)
        .build();

    var distanceFromSearchResponse2 = DistanceFromSearchResponse.builder()
        .hotelId("LONSTM")
        .distance("906")
        .name("hub London Covent Garden")
        .brand("HUB")
        .location(location)
        .build();

    return List.of(distanceFromSearchResponse1, distanceFromSearchResponse2);
  }

}