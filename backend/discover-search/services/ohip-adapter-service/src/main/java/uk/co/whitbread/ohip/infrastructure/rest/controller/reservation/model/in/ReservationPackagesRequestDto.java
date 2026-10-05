package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPackagesRequestDto {

  @NotNull
  @Valid
  @Schema(required = true)
  private List<String> reservationsId;

  @Schema(example = "LONSTM")
  private String hotelId;

  @NotNull
  @Schema(example = "2015-10-21", required = true)
  private String departure;

  @NotNull
  @Schema(example = "2015-10-20", required = true)
  private String arrival;

  @Valid
  private List<RoomsSelectionsDto> roomsSelections;

  @Valid
  private List<RoomsSelectionsDto> previousRoomsSelections;
}
