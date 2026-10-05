package uk.co.whitbread.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import uk.co.whitbread.domain.ports.secondary.DistanceFromSearchOutPort;

@ExtendWith(MockitoExtension.class)
class DistanceFromSearchInPortImplTest {

  private static final String HOTEL_ID = "LONEUS";
  private static final String LOCATION = "ChIJdd4hrwug2EcRmSrV3Vo6llI";
  private static final String LOCATION_FORMAT = "placeId";
  private static final Integer RADIUS = 20;
  private static final String RADIUS_UNIT_MILES = "mi";

  private static final String RADIUS_UNIT_KILOMETERS = "km";

  @InjectMocks
  private DistanceFromSearchInPortImpl distanceFromSearchInPort;

  @Mock
  private DistanceFromSearchOutPort distanceFromSearchOutPort;

  @Test
  void getDistanceFromSearchInMiles__ShouldReturnOk() {
    //Arrange
    var response = buildDistanceFromSearchResponse(true);
    var request = buildDistanceFromSearchRequestRequest(true);

    when(this.distanceFromSearchOutPort.getDistanceFromSearch(
        any(DistanceFromSearchRequest.class))).thenReturn(response);

    //Act
    final var hotelDistanceResponse = distanceFromSearchInPort.getDistanceFromSearch(request);

    //Assert
    assertThat(hotelDistanceResponse, notNullValue());
    MatcherAssert.assertThat(hotelDistanceResponse.getName(), is("London Euston"));
    MatcherAssert.assertThat(hotelDistanceResponse.getHotelId(), is("LONEUS"));
    MatcherAssert.assertThat(hotelDistanceResponse.getDistance(), is("1.8"));
    MatcherAssert.assertThat(hotelDistanceResponse.getBrand(), is("PI"));
  }

  @Test
  void getDistanceFromSearchInKms__ShouldReturnOk() {
    //Arrange
    var response = buildDistanceFromSearchResponse(false);
    var request = buildDistanceFromSearchRequestRequest(false);

    when(this.distanceFromSearchOutPort.getDistanceFromSearch(
        any(DistanceFromSearchRequest.class))).thenReturn(response);

    //Act
    final var hotelDistanceResponse = distanceFromSearchInPort.getDistanceFromSearch(request);

    //Assert
    assertThat(hotelDistanceResponse, notNullValue());
    MatcherAssert.assertThat(hotelDistanceResponse.getName(), is("London Euston"));
    MatcherAssert.assertThat(hotelDistanceResponse.getHotelId(), is("LONEUS"));
    MatcherAssert.assertThat(hotelDistanceResponse.getDistance(), is("0.0"));
    MatcherAssert.assertThat(hotelDistanceResponse.getBrand(), is("PI"));
  }

  @Test
  void getHotelsLocations__ShouldReturnOk() {
    //Arrange
    var response = buildDistanceFromSearchListResponse();
    var request = buildDistanceFromSearchRequestRequest(true);

    when(this.distanceFromSearchOutPort.getHotelDistancesFromSearch(
        any(DistanceFromSearchRequest.class))).thenReturn(response);

    //Act
    final var hotelsLocationsResponse = distanceFromSearchInPort.getHotelDistancesFromSearch(
        request);

    //Assert
    assertThat(hotelsLocationsResponse, notNullValue());
    MatcherAssert.assertThat(hotelsLocationsResponse, hasSize(2));

    var hotelLocation = hotelsLocationsResponse.get(0);
    assertThat(hotelLocation, notNullValue());

    MatcherAssert.assertThat(hotelLocation.getName(), is("London Leicester Square"));
    MatcherAssert.assertThat(hotelLocation.getHotelId(), is("LONLEI"));
    MatcherAssert.assertThat(hotelLocation.getDistance(), is("0.45"));
    MatcherAssert.assertThat(hotelLocation.getBrand(), is("PI"));
    MatcherAssert.assertThat(hotelLocation.getLocation(), notNullValue());
  }

  private DistanceFromSearchRequest buildDistanceFromSearchRequestRequest(boolean isMilesUnit) {
    return DistanceFromSearchRequest
        .builder()
        .hotelId(HOTEL_ID)
        .location(LOCATION)
        .locationFormat(LOCATION_FORMAT)
        .radius(RADIUS)
        .radiusUnit((isMilesUnit) ? RADIUS_UNIT_MILES : RADIUS_UNIT_KILOMETERS)
        .build();
  }

  private DistanceFromSearchResponse buildDistanceFromSearchResponse(boolean isMilesUnit) {
    return DistanceFromSearchResponse.builder()
        .hotelId("LONEUS")
        .distance((isMilesUnit) ? "2896" : "002")
        .name("London Euston")
        .brand("PI")
        .build();
  }

  private List<DistanceFromSearchResponse> buildDistanceFromSearchListResponse() {

    var location = HotelLocation.builder()
        .latitude(51.511143)
        .longitude(-0.13035)
        .build();

    var distanceFromSearchResponse1 = DistanceFromSearchResponse.builder()
        .hotelId("LONLEI")
        .distance("729")
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