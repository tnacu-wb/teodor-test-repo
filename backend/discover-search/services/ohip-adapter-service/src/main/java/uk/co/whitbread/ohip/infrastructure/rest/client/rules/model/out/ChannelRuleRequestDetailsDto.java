package uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRuleRequestDetailsDto {

  private String channel;
  private String subchannel;
  private String language;
  private String pms;
}