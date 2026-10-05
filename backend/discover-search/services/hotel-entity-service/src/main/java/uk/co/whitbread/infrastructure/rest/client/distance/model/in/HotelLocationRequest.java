package uk.co.whitbread.infrastructure.rest.client.distance.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HotelLocationRequest {

  private String hotelId;
  private String location;
  private String locationFormat;
  private Integer radius;
  private String radiusUnit;
}
