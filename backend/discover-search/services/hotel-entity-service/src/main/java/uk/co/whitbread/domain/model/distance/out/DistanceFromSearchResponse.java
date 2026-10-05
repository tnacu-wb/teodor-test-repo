package uk.co.whitbread.domain.model.distance.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DistanceFromSearchResponse {

  private String hotelId;
  private String distance;
  private String name;
  private String brand;
  private HotelLocation location;
}
