package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
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

  public ConfirmReservationRequest(String reservationId, String hotelId,
      PaymentOption paymentOption, String paymentMethod, String digitalPaymentMethod,
      String paymentType, PaymentCard paymentCard, String paymentId, Boolean pibaCardPresent,
      String ccAgentId, String threeDSIndicator) {
    this.reservationId = reservationId;
    this.hotelId = hotelId;
    this.paymentCard = paymentCard;
    this.paymentOption = paymentOption;
    this.paymentMethod = paymentMethod;
    this.digitalPaymentMethod = digitalPaymentMethod;
    this.paymentType = paymentType;
    this.paymentId = paymentId;
    this.pibaCardPresent = pibaCardPresent;
    this.ccAgentId = ccAgentId;
    this.threeDSIndicator = threeDSIndicator;
    this.validateSelf();
  }
}
