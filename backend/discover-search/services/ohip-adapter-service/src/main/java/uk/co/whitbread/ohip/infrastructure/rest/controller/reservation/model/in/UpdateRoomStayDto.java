package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import lombok.Data;

@Data
public class UpdateRoomStayDto {

  private String arrivalDate;
  private String departureDate;
  private UpdateRoomOccupancyDto roomOccupancy;

  private List<UpdateRoomRateDto> roomRates;
}
