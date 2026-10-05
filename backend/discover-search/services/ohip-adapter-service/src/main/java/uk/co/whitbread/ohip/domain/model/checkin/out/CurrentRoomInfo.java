package uk.co.whitbread.ohip.domain.model.checkin.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentRoomInfo {

  private String roomType;
  private String roomId;
  private List<String> suggestedRoomNumbers;
  private String roomOwnershipType;

}
