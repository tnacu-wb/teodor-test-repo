package uk.co.whitbread.infrastructure.rest.client.rulesagent.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomSubstitutionRuleRequestDto {

  private Integer adults;
  private Integer children;
  private String roomType;
  private String pms;
  private String channel;
}
