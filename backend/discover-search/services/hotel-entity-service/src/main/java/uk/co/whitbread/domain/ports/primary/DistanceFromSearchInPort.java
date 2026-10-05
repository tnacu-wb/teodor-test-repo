package uk.co.whitbread.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.domain.model.distance.in.DistanceFromSearchRequest;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;

public interface DistanceFromSearchInPort {

  DistanceFromSearchResponse getDistanceFromSearch(
      DistanceFromSearchRequest distanceFromSearchRequest);

  List<DistanceFromSearchResponse> getHotelDistancesFromSearch(
      DistanceFromSearchRequest distanceFromSearchRequest);
}
