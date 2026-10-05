package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomStayRequest {
  private String arrivalDate;
  private String departureDate;
  private UpdateRoomOccupancyRequest roomOccupancy;
  private List<UpdateRoomRateRequest> roomRates;
}
