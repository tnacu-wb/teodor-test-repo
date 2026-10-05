package uk.co.whitbread.digitalkey.domain.model.checkin.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

  private List<ReservationIdList> reservationIdList;
  private RoomStay roomStay;
  private List<ReservationPackages> reservationPackages;
  private List<String> alerts;
  private String hotelId;

}
