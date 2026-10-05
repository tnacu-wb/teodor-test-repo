package uk.co.whitbread.reservation.domain.model.searchrules.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchRules {

  private Integer maxArrivalDate;
  private Integer maxNights;
  private Integer maxRooms;
  private Integer maxRoomsAmend;
  private List<RoomOccupancy> roomOccupancies = null;
}
