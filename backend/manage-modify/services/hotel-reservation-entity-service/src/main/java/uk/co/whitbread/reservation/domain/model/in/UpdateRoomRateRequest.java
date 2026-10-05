package uk.co.whitbread.reservation.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomRateRequest {
  private String operaRoomType;
  private String roomType;
  private String ratePlanCode;
  private UpdateRoomOccupancyRequest roomOccupancy;
  private String startDate;
  private String endDate;

  private RateType rates;
  private Boolean fixedRate;
  private RoomRate roomRate;
}
