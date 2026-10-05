package uk.co.whitbread.ohip.domain.model.roomallocation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Housekeeping {

  private RoomStatusResponse roomStatus;
  private HousekeepingRoomStatus housekeepingRoomStatus;
  private RoomCondition roomCondition;

}
