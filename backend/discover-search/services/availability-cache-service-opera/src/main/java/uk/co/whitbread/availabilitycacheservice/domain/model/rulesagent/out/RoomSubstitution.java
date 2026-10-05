package uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomSubstitution {

  private String type;
  private Boolean silent;
  private String specialRequest;
  private String accessibleSpecialRequest;
  private String codePackage;
}
