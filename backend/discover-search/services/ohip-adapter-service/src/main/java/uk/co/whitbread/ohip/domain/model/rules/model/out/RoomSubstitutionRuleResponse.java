package uk.co.whitbread.ohip.domain.model.rules.model.out;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomSubstitutionRuleResponse {

  private RoomSubstitutionRequestDetails requestDetails;
  private Date generatedAt;
  private List<RoomSubstitution> substitutionList;

}
