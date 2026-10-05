package uk.co.whitbread.kiosk.domain.model.roomallocation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseKeepingResponse {

  private String hotelId;
  private String roomId;
  private String status;


}
