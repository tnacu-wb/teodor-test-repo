package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;

public interface RulesAgentInPort {

  RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest);


}
