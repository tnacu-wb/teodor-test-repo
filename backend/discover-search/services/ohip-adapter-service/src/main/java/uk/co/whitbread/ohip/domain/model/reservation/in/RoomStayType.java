package uk.co.whitbread.ohip.domain.model.reservation.in;


import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomStayType {

  private List<RoomRateType> roomRates;
  private String arrivalDate;
  private String departureDate;
}
