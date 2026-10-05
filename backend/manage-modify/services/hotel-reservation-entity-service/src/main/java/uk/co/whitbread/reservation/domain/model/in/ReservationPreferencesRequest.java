package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.validator.NoEmptyStrings;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPreferencesRequest {

  @NotNull
  @NotEmpty
  private String hotelId;

  @NotNull
  @NotEmpty
  @NoEmptyStrings
  private List<String> reservationsIds;

  @Valid
  private List<PreferencesCollection> preferencesCollections;
}
