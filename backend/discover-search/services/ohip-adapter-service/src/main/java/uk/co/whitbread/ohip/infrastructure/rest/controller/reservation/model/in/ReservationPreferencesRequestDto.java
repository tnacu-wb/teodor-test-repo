package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.NoEmptyStrings;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPreferencesRequestDto {

  @Schema(example = "LONSTM")
  @NotNull
  @NotEmpty
  private String hotelId;

  @NotNull
  @NotEmpty
  @NoEmptyStrings
  private List<String> reservationsIds;

  @Valid
  private List<PreferencesCollectionDto> preferencesCollections;
}
