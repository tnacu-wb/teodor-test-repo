package uk.co.whitbread.rules.agent.domain.model.out;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RoomSubstitution {

  String type;
  Boolean silent;
  String specialRequest;
  String accessibleSpecialRequest;
  String codePackage;

}
