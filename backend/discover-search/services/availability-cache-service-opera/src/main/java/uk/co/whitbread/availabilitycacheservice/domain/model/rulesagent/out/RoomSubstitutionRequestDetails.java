package uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomSubstitutionRequestDetails {

  private Integer adults;
  private Integer children;
  private Boolean cotRequired;
  private String roomType;
  private String pms;
  private String channel;
}
