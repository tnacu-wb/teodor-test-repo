package uk.co.whitbread.content.domain.model.dlp.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Coordinates {

  private Double latitude;
  private Double longitude;
  private String radius;
  private Boolean hideHotelDistance;
}
