package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomConditionValueDto {

  private String code;
  private String description;

}
