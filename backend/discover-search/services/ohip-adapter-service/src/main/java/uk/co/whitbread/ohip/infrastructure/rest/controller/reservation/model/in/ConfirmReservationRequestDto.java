package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmReservationRequestDto {

  @NotNull
  private String reservationId;
  @NotNull
  private String hotelId;
  @NotNull
  private PaymentOptionDto paymentOption;
  private String paymentMethod;
  private String digitalPaymentMethod;
  private String paymentType;
  private PaymentCardDto paymentCard;
  private String paymentId;
  private Boolean pibaCardPresent;
  private String ccAgentId;
  private String threeDSIndicator;
}
