package uk.co.whitbread.ohip.domain.model.roomallocation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HousekeepingRooms {

  private List<HouseKeepingRoom> room;
  private String hotelId;

}
