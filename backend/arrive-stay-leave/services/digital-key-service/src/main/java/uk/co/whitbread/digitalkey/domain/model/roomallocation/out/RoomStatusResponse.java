package uk.co.whitbread.digitalkey.domain.model.roomallocation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomStatusResponse {

  private String roomStatus;
  private String frontOfficeStatus;

}
