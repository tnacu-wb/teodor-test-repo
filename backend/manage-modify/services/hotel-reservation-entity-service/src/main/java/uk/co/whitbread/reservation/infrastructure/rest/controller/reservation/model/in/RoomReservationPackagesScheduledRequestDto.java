package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomReservationPackagesScheduledRequestDto {

  @NotEmpty
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String reservationsId;

  @NotNull
  @Valid
  private List<PackagesSelectionScheduledDto> addPackages;

  @NotNull
  @Valid
  private List<PackagesSelectionDto> removePackages;

}
