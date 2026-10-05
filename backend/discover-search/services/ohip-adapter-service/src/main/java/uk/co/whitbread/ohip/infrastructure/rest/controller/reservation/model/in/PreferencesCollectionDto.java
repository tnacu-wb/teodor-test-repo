package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

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
public class PreferencesCollectionDto {

  @NotNull
  @NotEmpty
  private String preferenceType;

  @NotNull
  @NoEmptyStrings
  private List<String> preferences;
}
