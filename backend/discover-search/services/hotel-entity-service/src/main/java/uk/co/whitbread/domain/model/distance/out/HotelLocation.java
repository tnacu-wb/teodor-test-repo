package uk.co.whitbread.domain.model.distance.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HotelLocation {

  private Double latitude;
  private Double longitude;
}
