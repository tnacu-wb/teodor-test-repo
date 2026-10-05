package uk.co.whitbread.rules.agent.domain.model.out;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class RoomSubstitutionRequestDetails {

  Integer adults;
  Integer children;
  String roomType;
  String pms;
  String channel;
}
