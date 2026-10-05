package uk.co.whitbread.ohip.domain.model.reservation.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPackagesRequest {

  @NotNull
  @Valid
  @Schema(required = true)
  private List<String> reservationsId;

  @Schema(example = "LONSTM")
  private String hotelId;

  @NotNull
  @Schema(example = "2015-10-20", required = true)
  private String arrival;

  @NotNull
  @Schema(example = "2015-10-21", required = true)
  private String departure;

  @Valid
  private List<RoomsSelections> roomsSelections;

  @Valid
  private List<RoomsSelections> previousRoomsSelections;
}
