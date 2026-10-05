package uk.co.whitbread.domain.model.rulesagent.out;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomSubstitutionRuleResponse {

  private RoomSubstitutionRequestDetails requestDetails;
  private Date generatedAt;
  private List<RoomSubstitution> substitutionList;
}
