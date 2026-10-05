package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Data;

@Data
public class UpdateReservationCcAgentIdRequestDto {

  @NotNull
  @Size(min = 1)
  @Valid
  @Schema(requiredMode = RequiredMode.REQUIRED)
  private Set<String> reservationIds;

  @NotNull
  @Valid
  @Schema(example = "LONEUS", requiredMode = RequiredMode.REQUIRED)
  private String hotelId;

  @NotEmpty
  @Schema(example = "jane.doe@wb.com", requiredMode = RequiredMode.REQUIRED)
  private String ccAgentId;

  private boolean clearFirst;
}
