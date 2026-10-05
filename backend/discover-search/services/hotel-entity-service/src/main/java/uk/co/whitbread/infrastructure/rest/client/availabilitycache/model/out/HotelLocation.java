package uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HotelLocation {

  private Double latitude;
  private Double longitude;
}
