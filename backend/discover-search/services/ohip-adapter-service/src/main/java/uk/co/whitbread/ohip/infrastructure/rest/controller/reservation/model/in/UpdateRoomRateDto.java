package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import lombok.Data;

@Data
public class UpdateRoomRateDto {

  private RateTypeDto rates;
  private String roomType;
  private String ratePlanCode;
  private UpdateRoomOccupancyDto roomOccupancy;
  private String startDate;
  private String endDate;
}
