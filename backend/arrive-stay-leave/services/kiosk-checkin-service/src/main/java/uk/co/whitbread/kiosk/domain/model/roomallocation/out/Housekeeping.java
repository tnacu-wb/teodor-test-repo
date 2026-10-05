package uk.co.whitbread.kiosk.domain.model.roomallocation.out;

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
  private RoomCondition roomCondition;

}
