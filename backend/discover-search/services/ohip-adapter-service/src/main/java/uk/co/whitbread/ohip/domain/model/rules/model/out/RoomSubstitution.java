package uk.co.whitbread.ohip.domain.model.rules.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomSubstitution {

  private String type;
  private Boolean silent;
  private String specialRequest;
  private String accessibleSpecialRequest;
  private String codePackage;

}
