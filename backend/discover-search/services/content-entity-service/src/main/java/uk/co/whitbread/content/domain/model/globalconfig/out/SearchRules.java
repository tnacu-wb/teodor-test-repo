package uk.co.whitbread.content.domain.model.globalconfig.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRules {

  private Integer maxRooms;

  private Integer maxRoomsAmend;

  private Integer maxNights;

  private Integer maxArrivalDate;

  private List<AcceptedRoomTypes> roomOccupancies;
}
