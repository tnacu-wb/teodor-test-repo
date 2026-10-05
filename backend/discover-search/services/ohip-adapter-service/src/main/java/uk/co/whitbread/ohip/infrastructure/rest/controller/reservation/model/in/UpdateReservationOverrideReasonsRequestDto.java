package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Data;

@Data
public class UpdateReservationOverrideReasonsRequestDto {

  @NotNull
  @Size(min = 1)
  @Valid
  @Schema(required = true)
  private Set<String> reservationIds;

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
