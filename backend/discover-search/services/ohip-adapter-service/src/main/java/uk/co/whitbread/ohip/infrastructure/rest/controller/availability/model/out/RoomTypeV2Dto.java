package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeV2Dto {

  private String tag;
  private String roomType;
  private String adults;
  private String children;
  private String numberOfRooms;
  private List<String> specialRequests;
  private List<RoomRateV2Dto> roomRates;
}
