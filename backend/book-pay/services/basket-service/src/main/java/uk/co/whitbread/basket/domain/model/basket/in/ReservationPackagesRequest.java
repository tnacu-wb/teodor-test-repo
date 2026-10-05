package uk.co.whitbread.basket.domain.model.basket.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ReservationPackagesRequest {

  @Schema(example = "LONSTM")
  private String hotelId;

  @NotNull
  @Schema(example = "2015-10-20", required = true)
  private String arrivalDate;

  @NotNull
  @Schema(example = "2015-10-21", required = true)
  private String departureDate;

  private List<String> reservationsId;

  @Valid
  private List<RoomsSelections> roomsSelections;
}
