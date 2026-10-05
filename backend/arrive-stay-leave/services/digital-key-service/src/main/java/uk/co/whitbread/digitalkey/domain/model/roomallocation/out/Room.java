package uk.co.whitbread.digitalkey.domain.model.roomallocation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

  private String floor;
  private String floorDescription;
  private String smokingPreference;
  private String smokingPreferenceDescription;
  private String roomId;
  private Housekeeping housekeeping;

}
