package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomStay {

  private String arrivalDate;
  private String departureDate;
  private RoomOccupancy roomOccupancy;

  private List<RoomRate> roomRates;
}
