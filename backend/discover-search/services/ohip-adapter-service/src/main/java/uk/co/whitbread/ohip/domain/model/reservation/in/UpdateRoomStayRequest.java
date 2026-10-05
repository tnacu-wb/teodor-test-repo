package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomStayRequest {

  private String arrivalDate;

  private String departureDate;

  private RoomOccupancy roomOccupancy;

  private List<UpdateRoomRateRequest> roomRates;

}