package uk.co.whitbread.infrastructure.rest.controller.lov.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancellationReasonDto {

  private String code;
  private String name;
  private String description;
  private boolean active;
  private boolean managerApprovalNeeded;

}
