package uk.co.whitbread.infrastructure.rest.client.distance.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelLocationResponse {

  private String code;
  private String distance;
  private String name;
  private String brand;
  private MapLocation location;
}
