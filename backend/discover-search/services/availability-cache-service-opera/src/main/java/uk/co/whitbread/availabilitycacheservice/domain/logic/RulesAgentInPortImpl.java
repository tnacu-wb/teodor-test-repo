package uk.co.whitbread.availabilitycacheservice.domain.logic;

import lombok.RequiredArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.RulesAgentInPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.RulesAgentOutPort;

@RequiredArgsConstructor
public class RulesAgentInPortImpl implements RulesAgentInPort {

  private final RulesAgentOutPort rulesAgentOutPort;

  @Override
  public RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest) {
    return rulesAgentOutPort.getRoomSubstitutionRule(roomSubstitutionRuleRequest);
  }
}
