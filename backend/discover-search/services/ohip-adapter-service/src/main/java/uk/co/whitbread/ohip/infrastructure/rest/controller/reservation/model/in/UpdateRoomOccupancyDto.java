package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import lombok.Data;

@Data
public class UpdateRoomOccupancyDto {

  private Integer adultCount;
  private Integer childCount;
}
