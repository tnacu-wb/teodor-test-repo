package uk.co.whitbread.ohip.domain.model.availability.out;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResult {

  private Instant timestamp;
  private String hotelId;
  private String startDate;
  private String endDate;
  private boolean available;
  private boolean limitedAvailability;
  private List<RoomSubstitution> substitutionList;

  @Singular
  private List<AvailabilityRoomRate> roomRates = new ArrayList<>();
}
