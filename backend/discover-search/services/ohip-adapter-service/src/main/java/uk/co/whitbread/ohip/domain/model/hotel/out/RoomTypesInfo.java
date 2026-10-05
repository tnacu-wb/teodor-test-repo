package uk.co.whitbread.ohip.domain.model.hotel.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypesInfo {

  private List<RoomTypeInfo> roomType;
  private String hotelId;
}
