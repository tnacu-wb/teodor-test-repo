package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ConfirmReservationRequest implements SelfValidation<ConfirmReservationRequest> {

  @NotEmpty
  private String reservationId;
  @NotEmpty
  private String hotelId;
  @NotNull
  private PaymentOption paymentOption;
  private String paymentMethod;
  private String digitalPaymentMethod;
  private String paymentType;
  private PaymentCard paymentCard;
  private String paymentId;
  private Boolean pibaCardPresent;
  private String ccAgentId;
  private String threeDSIndicator;
}
