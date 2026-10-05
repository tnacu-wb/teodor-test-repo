package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRulesDto {

  private Integer maxNights;

  private Integer maxRooms;

  private Integer maxRoomsAmend;

  private Integer maxArrivalDate;

  private List<AcceptedRoomTypesDto> roomOccupancies;
}
