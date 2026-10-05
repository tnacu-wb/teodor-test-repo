package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HotelReservationInstructionType {

  private List<UniqueIdType> reservationIdList;
  private RoomStayType roomStay;
  private String hotelId;
}
