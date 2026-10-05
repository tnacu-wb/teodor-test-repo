package uk.co.whitbread.ohip.domain.model.roomallocation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomConditionValue {

  private String code;
  private String description;

}
