package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomRateRequest {

  private String roomType;

  private String ratePlanCode;

  private RoomOccupancy roomOccupancy;

  private String startDate;

  private String endDate;

}