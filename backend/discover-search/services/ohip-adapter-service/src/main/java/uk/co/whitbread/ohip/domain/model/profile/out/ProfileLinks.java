package uk.co.whitbread.ohip.domain.model.profile.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileLinks {

  private String href;
  private String rel;
  private boolean templated;
  private String method;
  private String operationId;

}
