package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomIdsDto {

  private List<String> roomIds;

}
