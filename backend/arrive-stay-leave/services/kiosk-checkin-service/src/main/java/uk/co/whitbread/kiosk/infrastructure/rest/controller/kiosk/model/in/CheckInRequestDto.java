package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRequestDto {

  private String reservationNumber;
  private String hotelId;
  private String roomType;
  private String roomId;
  private PaymentDetailsDto paymentDetails;
  private List<StayingGuestDetailsDto> stayingGuestDetails;
  private List<ReservationCommentsDto> reservationComments;
}
