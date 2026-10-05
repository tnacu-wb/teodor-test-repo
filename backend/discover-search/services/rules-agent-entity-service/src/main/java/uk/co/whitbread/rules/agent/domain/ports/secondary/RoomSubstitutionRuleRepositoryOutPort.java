package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.rules.agent.domain.model.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRule;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRule;

public interface RoomSubstitutionRuleRepositoryOutPort extends
    RuleEngineRepositoryOutPort<RbacRule> {

  List<RoomSubstitutionRule> findRoomSubstitutionRules(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest);
}
