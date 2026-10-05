package uk.co.whitbread.ohip.domain.model.lov.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancellationReason {

  private String code;
  private String name;
  private String description;
  private boolean active;
  private boolean managerApprovalNeeded;

}
