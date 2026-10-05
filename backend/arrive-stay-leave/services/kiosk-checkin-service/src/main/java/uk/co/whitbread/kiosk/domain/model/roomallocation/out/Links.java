package uk.co.whitbread.kiosk.domain.model.roomallocation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Links {

  private String href;
  private String rel;
  private boolean templated;
  private String method;
  private String operationId;

}
