package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.generated.models.reservation.RoomsSelectionsDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPackagesRequestDto {
  @JsonProperty("arrivalDate")
  private String arrivalDate;

  @JsonProperty("basketReferenceId")
  private String basketReferenceId;

  @JsonProperty("departureDate")
  private String departureDate;

  @JsonProperty("hotelId")
  private String hotelId;

  @JsonProperty("reservationsId")
  @Valid
  private List<String> reservationsId = null;

  @JsonProperty("previousRoomsSelections")
  @Valid
  private List<RoomsSelectionsDto> previousRoomsSelections = null;

  @JsonProperty("roomsSelections")
  @Valid
  private List<RoomsSelectionsDto> roomsSelections = null;
}
