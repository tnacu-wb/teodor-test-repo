package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CancelReservationRequest implements SelfValidation<CancelReservationRequest> {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private List<String> reservationIds;
  private PaymentOption paymentOption;
  private String defaultPaymentMethod;
  private String digitalPaymentMethod;
  private ReservationOverrideReason reservationOverrideReason;
  private Map<String, List<DepositFolioCharge>> chargesByReservationIds;

  public CancelReservationRequest(
      String hotelId, List<String> reservationIds, PaymentOption paymentOption,
      String defaultPaymentMethod, String digitalPaymentMethod,
      ReservationOverrideReason reservationOverrideReason,
      Map<String, List<DepositFolioCharge>> chargesByReservationIds) {
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
    this.paymentOption = paymentOption;
    this.defaultPaymentMethod = defaultPaymentMethod;
    this.digitalPaymentMethod = digitalPaymentMethod;
    this.reservationOverrideReason = reservationOverrideReason;
    this.chargesByReservationIds = chargesByReservationIds;
    this.validateSelf();
  }

}
