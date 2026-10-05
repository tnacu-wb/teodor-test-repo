package uk.co.whitbread.ohip.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {

  String roomType;
  Integer numberOfRooms;
  String adults;
  String children;
  String cotRequested;
  List<RoomRateInfo> roomRates;

}
