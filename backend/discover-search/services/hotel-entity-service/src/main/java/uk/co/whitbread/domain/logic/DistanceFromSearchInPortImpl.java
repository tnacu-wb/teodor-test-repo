package uk.co.whitbread.domain.logic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.domain.model.distance.in.DistanceFromSearchRequest;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.ports.primary.DistanceFromSearchInPort;
import uk.co.whitbread.domain.ports.secondary.DistanceFromSearchOutPort;

@Slf4j
@RequiredArgsConstructor
public class DistanceFromSearchInPortImpl implements DistanceFromSearchInPort {

  private final DistanceFromSearchOutPort distanceFromSearchOutPort;

  @Override
  public DistanceFromSearchResponse getDistanceFromSearch(
      DistanceFromSearchRequest distanceFromSearchRequest) {
    log.debug("Entered getDistanceFromSearch with hotelId={}, location={}, locationFormat={},"
            + " radius={}, radiusUnit={}", distanceFromSearchRequest.getHotelId(),
        distanceFromSearchRequest.getLocation(),
        distanceFromSearchRequest.getLocationFormat(), distanceFromSearchRequest.getRadius(),
        distanceFromSearchRequest.getRadiusUnit());

    DistanceFromSearchResponse distanceFromSearchResponse = distanceFromSearchOutPort
        .getDistanceFromSearch(distanceFromSearchRequest);

    convertDistance(distanceFromSearchResponse, distanceFromSearchRequest.getRadiusUnit());
    return distanceFromSearchResponse;
  }

  @Override
  public List<DistanceFromSearchResponse> getHotelDistancesFromSearch(
      DistanceFromSearchRequest distanceFromSearchRequest) {
    log.debug(
        "Entered getHotelDistancesFromSearch with hotelId={}, location={}, locationFormat={},"
            + " radius={}, radiusUnit={}", distanceFromSearchRequest.getHotelId(),
        distanceFromSearchRequest.getLocation(),
        distanceFromSearchRequest.getLocationFormat(), distanceFromSearchRequest.getRadius(),
        distanceFromSearchRequest.getRadiusUnit());

    List<DistanceFromSearchResponse> distanceFromSearchResponseList = distanceFromSearchOutPort
        .getHotelDistancesFromSearch(distanceFromSearchRequest);

    convertDistance(distanceFromSearchResponseList, distanceFromSearchRequest.getRadiusUnit());
    return distanceFromSearchResponseList;
  }

  private void convertDistance(List<DistanceFromSearchResponse> responseList, String radiusUnit) {
    responseList.forEach(response -> convertDistance(response, radiusUnit));
  }

  private void convertDistance(DistanceFromSearchResponse response, String radiusUnit) {
    int distance = Integer.parseInt(response.getDistance());
    double convertedDistance = DistanceConverter.convertDistance(distance, radiusUnit);
    response.setDistance(Double.toString(convertedDistance));
  }
}
