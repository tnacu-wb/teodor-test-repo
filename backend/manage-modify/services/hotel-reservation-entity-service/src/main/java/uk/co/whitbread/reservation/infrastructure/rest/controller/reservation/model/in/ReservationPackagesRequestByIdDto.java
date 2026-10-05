package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ReservationPackagesRequestByIdDto {

  @NotNull
  @Valid
  @JsonProperty("basketReferenceId")
  @Schema(required = true)
  private String basketReference;

  @Schema(example = "LONSTM")
  private String hotelId;

  @NotNull
  @JsonProperty("arrivalDate")
  @Schema(example = "2015-10-20", required = true)
  private String arrival;

  @NotNull
  @JsonProperty("departureDate")
  @Schema(example = "2015-10-21", required = true)
  private String departure;

  @Valid
  private List<RoomsSelectionsByReservationIdDto> roomsSelections;

  @Valid
  private List<RoomsSelectionsByReservationIdDto> previousRoomsSelections;
}
