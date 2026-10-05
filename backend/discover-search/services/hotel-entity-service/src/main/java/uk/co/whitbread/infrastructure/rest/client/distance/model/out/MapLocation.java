package uk.co.whitbread.infrastructure.rest.client.distance.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MapLocation {

  private Double latitude;
  private Double longitude;
}
