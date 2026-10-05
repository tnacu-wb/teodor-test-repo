package uk.co.whitbread.ohip.domain.ports.secondary;

import uk.co.whitbread.ohip.domain.model.rules.model.out.BaseRateRuleResponse;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;

public interface RulesAgentOutPort {

  RoomSubstitutionRuleResponse getRoomSubstitution(String roomType, Integer adultsNumber,
                                                   Integer childrenNumber, String channel);
  
  BaseRateRuleResponse getBaseRate(String ratePlanCode);
}
