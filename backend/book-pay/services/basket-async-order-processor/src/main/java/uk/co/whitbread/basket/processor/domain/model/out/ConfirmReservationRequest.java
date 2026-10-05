package uk.co.whitbread.basket.processor.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
  private String ccAgentId;
  private String threeDSIndicator;
}
