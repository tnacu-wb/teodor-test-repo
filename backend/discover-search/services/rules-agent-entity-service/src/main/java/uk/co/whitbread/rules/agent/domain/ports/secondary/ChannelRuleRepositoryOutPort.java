package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRule;

public interface ChannelRuleRepositoryOutPort extends RuleEngineRepositoryOutPort<ChannelRule> {

  Optional<ChannelRule> findRule(ChannelRuleRequest channelRuleRequest);

  Optional<ChannelRule> findRule(String sourceId);

}
