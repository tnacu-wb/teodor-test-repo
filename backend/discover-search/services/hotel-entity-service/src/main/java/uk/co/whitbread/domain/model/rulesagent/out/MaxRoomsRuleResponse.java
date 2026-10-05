package uk.co.whitbread.domain.model.rulesagent.out;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaxRoomsRuleResponse {

  private Integer maxRooms;
  private MaxRoomsRequestDetails requestDetails;
  private Date generatedAt;
}
