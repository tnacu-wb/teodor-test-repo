package uk.co.whitbread.kiosk.domain.model.roomallocation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KioskReservationPreferences {

  private List<KioskPreferenceCollection> kioskPreferenceCollection;
}
