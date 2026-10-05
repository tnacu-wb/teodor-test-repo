package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateReservationOverrideReasonsRequestDto {

  @NotEmpty
  @Schema(example = "GBM6919649", required = true)
  private String basketReference;

  @NotNull
  @Valid
  @Schema(example = "LONEUS", required = true)
  private String hotelId;

  @NotEmpty
  @Schema(example = "ILL", required = true)
  private String reasonCode;

  @NotEmpty
  @Schema(example = "Medical Appointments", required = true)
  private String reasonName;

  @NotEmpty
  @Schema(example = "John Doe", required = true)
  private String callerName;

  private String managerName;

}
