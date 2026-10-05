package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RoomSubstitutionDto {

  String type;
  Boolean silent;
  String specialRequest;
  @JsonInclude(Include.NON_EMPTY)
  String accessibleSpecialRequest;
  @JsonInclude(Include.NON_EMPTY)
  String codePackage;

}
