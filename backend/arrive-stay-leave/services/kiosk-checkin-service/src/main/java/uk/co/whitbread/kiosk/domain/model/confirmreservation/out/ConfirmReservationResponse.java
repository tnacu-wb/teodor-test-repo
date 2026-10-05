package uk.co.whitbread.kiosk.domain.model.confirmreservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmReservationResponse {

  private List<ReservationIdList> reservationIdList;
  private ConfirmReservationRoomStay roomStay;
  private ReservationGuest reservationGuest;
  private String hotelId;
  private String reservationStatus;
  private boolean isPartialPaid;
}
