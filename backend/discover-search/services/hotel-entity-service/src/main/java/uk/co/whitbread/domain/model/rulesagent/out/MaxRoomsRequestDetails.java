package uk.co.whitbread.domain.model.rulesagent.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaxRoomsRequestDetails {

  private String channelId;
}
