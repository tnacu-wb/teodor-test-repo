package uk.co.whitbread.reservation.domain.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateReservationPackagesByIdRequest {

  @NotNull
  @Valid
  @Schema(required = true)
  private String basketReference;

  @Schema(example = "LONSTM")
  private String hotelId;

  @NotNull
  @Schema(example = "2015-10-20", required = true)
  private String arrival;

  @NotNull
  @Schema(example = "2015-10-21", required = true)
  private String departure;

  @Valid
  private List<RoomsSelectionsByReservationId> roomsSelections;

  @Valid
  private List<RoomsSelectionsByReservationId> previousRoomsSelections;
}
