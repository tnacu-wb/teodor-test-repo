package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.NoEmptyStrings;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreferencesCollection {

  @NotNull
  @NotEmpty
  private String preferenceType;

  @NotNull
  @NoEmptyStrings
  private List<String> preferences;
}
