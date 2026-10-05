package uk.co.whitbread.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeV2 {

  private String tag;
  private String roomType;
  private String adults;
  private String children;
  private String numberOfRooms;
  private List<String> specialRequests;
  private List<RoomRateV2> roomRates;
}
