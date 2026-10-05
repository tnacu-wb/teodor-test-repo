package uk.co.whitbread.domain.logic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.distance.in.DistanceFromSearchRequest;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.ports.secondary.DistanceFromSearchOutPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class SnowdropInformation {

  private final DistanceFromSearchOutPort snowdropOutPort;

  public List<DistanceFromSearchResponse> getHotelsFromSnowdrop(
          HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    DistanceFromSearchRequest distanceFromSearchRequest = DistanceFromSearchRequest.builder()
            .location(hotelAvailabilitiesRequest.getLocation())
            .locationFormat(hotelAvailabilitiesRequest.getLocationFormat().name())
            .radius(hotelAvailabilitiesRequest.getRadius())
            .radiusUnit(AvailabilitiesResponseUtils.buildRadiusUnit(hotelAvailabilitiesRequest.getRadiusUnit()))
            .build();
    return snowdropOutPort.getHotelDistancesFromSearch(distanceFromSearchRequest);
  }

  public List<String> getSnowdropHotelIds(
          List<DistanceFromSearchResponse> snowdropHotelList) {
    var snowdropHotelIds = snowdropHotelList
            .stream()
            .map(DistanceFromSearchResponse::getHotelId)
            .toList();

    log.info("The following Snowdrop hotel ids={} were found", snowdropHotelIds);
    return snowdropHotelIds;
  }
}
