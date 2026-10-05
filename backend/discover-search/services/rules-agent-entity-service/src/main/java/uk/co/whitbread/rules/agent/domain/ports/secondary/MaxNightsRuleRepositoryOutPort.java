package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRule;


public interface MaxNightsRuleRepositoryOutPort extends RuleEngineRepositoryOutPort<MaxNightsRule> {

  Optional<MaxNightsRule> findRule(String channelId);
}
