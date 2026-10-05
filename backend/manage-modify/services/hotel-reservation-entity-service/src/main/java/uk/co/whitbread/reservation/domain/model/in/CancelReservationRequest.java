package uk.co.whitbread.reservation.domain.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CancelReservationRequest implements SelfValidation<ConfirmReservationRequest> {

  @NotEmpty
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String basketReference;

  private List<String> reservationIds;

  @NotEmpty
  @Schema(example = "LONSTM", requiredMode = Schema.RequiredMode.REQUIRED)
  private String hotelId;
  private PaymentOption paymentOption;
  private String defaultPaymentMethod;
  private String digitalPaymentMethod;
  private ReservationOverrideReason reservationOverrideReason;

  private String token;
}
