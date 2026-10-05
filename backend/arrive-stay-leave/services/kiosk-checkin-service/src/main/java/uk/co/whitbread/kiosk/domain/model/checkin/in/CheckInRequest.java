package uk.co.whitbread.kiosk.domain.model.checkin.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRequest {

  private String reservationNumber;
  private String hotelId;
  private String roomType;
  private String roomId;
  private PaymentDetails paymentDetails;
  private List<StayingGuestDetails> stayingGuestDetails;
  private List<ReservationComments> reservationComments;
}
