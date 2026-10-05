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
public class VacantRoomResponse {

  private HotelRoomsDetails hotelRoomsDetails;
  private int totalPages;
  private int offset;
  private int limit;
  private boolean hasMore;
  private int totalResults;
  private List<RoomLinks> links;


}
