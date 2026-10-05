package uk.co.whitbread.ohip.domain.model.roomallocation.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HousekeepingRoomStatus {

  @JsonProperty("housekeepingRoomStatus")
  private String housekeepingRoomStatusText;

}
