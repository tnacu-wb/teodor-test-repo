package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRule;

public interface MaxArrivalDateRuleRepositoryOutPort
    extends RuleEngineRepositoryOutPort<MaxArrivalDateRule> {

  Optional<MaxArrivalDateRule> findRule(String channelId);
}