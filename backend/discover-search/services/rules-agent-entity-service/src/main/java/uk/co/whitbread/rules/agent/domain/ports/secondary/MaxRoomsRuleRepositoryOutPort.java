package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRule;

public interface MaxRoomsRuleRepositoryOutPort extends RuleEngineRepositoryOutPort<MaxRoomsRule> {

  Optional<MaxRoomsRule> findRule(String channelId);
}