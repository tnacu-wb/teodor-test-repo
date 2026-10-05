package uk.co.whitbread.infrastructure.rest.controller.distance.model.out;

import lombok.Data;

@Data
public class DistanceFromSearchResponseDto {

  private String hotelId;
  private String distance;
  private String name;
  private String brand;
}
