package uk.co.whitbread.digitalkey.domain.model.roomallocation.out;

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

}
