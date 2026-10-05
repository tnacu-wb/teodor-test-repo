package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomAllocationResponseDto {

  private List<RoomLinksDto> links;

}
