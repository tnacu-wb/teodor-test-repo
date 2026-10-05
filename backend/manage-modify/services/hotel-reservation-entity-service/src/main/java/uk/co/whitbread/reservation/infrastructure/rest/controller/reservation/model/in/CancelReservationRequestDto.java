package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelReservationRequestDto {

  @NotEmpty
  @Valid
  @JsonProperty("basketReference")
  @Schema(required = true)
  private String basketReference;
  private List<String> reservationIds;
  @Schema(example = "LONSTM")
  private String hotelId;
  private PaymentOption paymentOption;
  @Valid
  private ReservationOverrideReasonDto reservationOverrideReason;
  private String token;
}
