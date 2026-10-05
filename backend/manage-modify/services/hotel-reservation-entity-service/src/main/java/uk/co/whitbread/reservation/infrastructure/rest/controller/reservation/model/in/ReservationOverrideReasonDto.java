package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationOverrideReasonDto {

  @NotNull
  @Schema(example = "ILL", required = true)
  private String reasonCode;
  @NotNull
  @Schema(example = "MA Illness", required = true)
  private String reasonName;
  @NotNull
  @Schema(example = "Jane Doe", required = true)
  private String callerName;
  @Schema(example = "John Smith")
  private String managerName;

}
