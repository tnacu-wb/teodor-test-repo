package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RoomSubstitutionRequestDetailsDto {

  Integer adults;
  Integer children;
  String roomType;
  String pms;
  String channel;
}
