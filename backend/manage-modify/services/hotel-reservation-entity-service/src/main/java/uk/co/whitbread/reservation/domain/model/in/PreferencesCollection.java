package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.validator.NoEmptyStrings;

@Data
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
