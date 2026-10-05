package uk.co.whitbread.ohip.domain.model.roomallocation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomsDetails {

  private List<KioskRoom> room;
  private String hotelId;

}
