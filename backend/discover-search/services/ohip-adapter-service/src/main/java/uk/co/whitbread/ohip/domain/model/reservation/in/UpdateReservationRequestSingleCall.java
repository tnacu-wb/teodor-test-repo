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
public class UpdateReservationRequestSingleCall {
  private String hotelId;
  private String reservationId;
  private UpdateRoomStayRequest roomStay;
  private List<ReservationGuestsSingleCall> reservationGuests;
}