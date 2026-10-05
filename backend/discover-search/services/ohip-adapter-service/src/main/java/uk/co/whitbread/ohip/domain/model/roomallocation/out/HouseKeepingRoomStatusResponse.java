package uk.co.whitbread.ohip.domain.model.roomallocation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseKeepingRoomStatusResponse {

  private HousekeepingRoomInfo housekeepingRoomInfo;


}
