package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Data;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentOption;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.DepositFolioChargeDto;

@Data
public class CancelReservationRequestDto {

  @NotEmpty
  @Schema(example = "LONSTM", requiredMode = Schema.RequiredMode.REQUIRED)
  private String hotelId;
  @NotNull
  @Valid
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private List<String> reservationIds;
  private PaymentOption paymentOption;
  private String defaultPaymentMethod;
  private String digitalPaymentMethod;
  @Valid
  private ReservationOverrideReasonDto reservationOverrideReason;
  private Map<String, List<DepositFolioChargeDto>> chargesByReservationIds;

}
