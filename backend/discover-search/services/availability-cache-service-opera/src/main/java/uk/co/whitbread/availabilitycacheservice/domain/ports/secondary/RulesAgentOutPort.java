package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;

public interface RulesAgentOutPort {

  RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest);

}
