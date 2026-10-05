package uk.co.whitbread.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultiRoomTypeInfo {

  String roomType;
  Integer numberOfRooms;
  String adults;
  String children;
  String cotRequested;
  List<RoomRateInfo> roomRates;

}
