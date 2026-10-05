package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KioskPreferenceCollectionDto {

  private List<KioskPreferenceDto> kioskPreference;
  private String preferenceType;
  private String preferenceTypeDescription;


}
