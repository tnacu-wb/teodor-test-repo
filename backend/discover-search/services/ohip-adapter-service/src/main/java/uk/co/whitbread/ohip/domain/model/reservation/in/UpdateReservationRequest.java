package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UpdateReservationRequest {

  private String hotelId;
  private ReservationType reservationType;
  private RoomStay roomStay;
  private List<ReservationGuests> reservationGuests;
  private Boolean sendEmailConfirmation;
  private Boolean sendEmailInvoice;
  private List<String> specialRequests;
}
