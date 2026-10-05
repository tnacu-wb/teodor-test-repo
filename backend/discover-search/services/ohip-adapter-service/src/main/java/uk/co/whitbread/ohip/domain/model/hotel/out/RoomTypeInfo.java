package uk.co.whitbread.ohip.domain.model.hotel.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeInfo {

  private String roomClass;
  private Boolean accessible;
  private String roomType;
  private String numberOfRooms;
}
