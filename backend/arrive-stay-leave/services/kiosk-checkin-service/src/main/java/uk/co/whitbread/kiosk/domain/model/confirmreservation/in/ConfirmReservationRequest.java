package uk.co.whitbread.kiosk.domain.model.confirmreservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmReservationRequest {

  private String hotelId;
  private String reservationId;
  private String paymentOption;
  private String paymentMethod;
  private String paymentType;
  private ConfirmReservationPaymentCard paymentCard;

}
