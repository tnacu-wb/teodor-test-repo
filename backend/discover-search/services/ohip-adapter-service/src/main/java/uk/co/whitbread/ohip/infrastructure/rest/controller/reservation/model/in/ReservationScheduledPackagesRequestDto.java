package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationScheduledPackagesRequestDto {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotEmpty
  private List<String> reservationsId;

  @NotEmpty
  @Schema(example = "2015-10-21", requiredMode = Schema.RequiredMode.REQUIRED)
  private String departure;

  @NotEmpty
  @Schema(example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  private String arrival;

  @Schema(example = "FRAMTI")
  private String hotelId;

  @Valid
  private List<RoomsScheduledSelectionsDto> roomsSelections;

  @Valid
  private List<RoomsScheduledSelectionsDto> previousRoomsSelections;
}
