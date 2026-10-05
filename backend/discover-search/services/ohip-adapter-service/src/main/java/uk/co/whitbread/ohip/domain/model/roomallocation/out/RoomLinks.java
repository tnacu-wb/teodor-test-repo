package uk.co.whitbread.ohip.domain.model.roomallocation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomLinks {

  private String href;
  private String rel;
  private boolean templated;
  private String method;
  private String operationId;

}
