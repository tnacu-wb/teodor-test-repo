package uk.co.whitbread.basket.domain.model.basket.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;

@Data
@AllArgsConstructor
@Builder
public class ConfirmReservationRequest {

  private String reservationId;
  private String hotelId;
  private PaymentOption paymentOption;
  private String paymentMethod;
  private String paymentType;
  private PaymentCard paymentCard;
  private String paymentId;
  private Boolean pibaCardPresent;
}
