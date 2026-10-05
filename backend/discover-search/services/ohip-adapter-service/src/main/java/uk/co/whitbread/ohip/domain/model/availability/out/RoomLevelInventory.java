package uk.co.whitbread.ohip.domain.model.availability.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomLevelInventory {

  private Integer availableCount;
  private String code;

}
