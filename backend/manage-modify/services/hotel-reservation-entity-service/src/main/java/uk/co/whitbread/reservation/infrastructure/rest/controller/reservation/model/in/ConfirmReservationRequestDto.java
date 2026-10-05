package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmReservationRequestDto {

  @NotEmpty
  private String reservationId;
  @NotEmpty
  private String hotelId;
  @NotNull
  private PaymentOptionDto paymentOption;
  private String paymentMethod;
  private String paymentType;
  private PaymentCardDto paymentCard;
  private String paymentId;
  private Boolean pibaCardPresent;
  private String ccAgentId;
  private String threeDSIndicator;
}
