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

public class UpdateReservationRequest {
  private String hotelId;
  private String reservationId;
  private UpdateRoomStayRequest roomStay;
  private List<ReservationGuests> reservationGuests;
}
