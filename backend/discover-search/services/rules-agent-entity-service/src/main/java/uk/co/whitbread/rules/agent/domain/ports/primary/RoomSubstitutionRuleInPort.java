package uk.co.whitbread.rules.agent.domain.ports.primary;

import uk.co.whitbread.rules.agent.domain.model.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRuleResponse;

public interface RoomSubstitutionRuleInPort {

  RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest);

}
