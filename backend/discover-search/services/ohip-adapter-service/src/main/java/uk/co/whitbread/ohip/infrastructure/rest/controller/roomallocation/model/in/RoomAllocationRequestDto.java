package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomAllocationRequestDto {

  private CriteriaDto criteria;

}
