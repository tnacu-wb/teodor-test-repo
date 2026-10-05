package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoordinatesDto {

  private Double latitude;
  private Double longitude;
  private String radius;
  private Boolean hideHotelDistance;
}
