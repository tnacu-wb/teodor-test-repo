package uk.co.whitbread.rules.agent.domain.ports.primary;

import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleResponse;

public interface ChannelRuleInPort {

  ChannelRuleResponse getChannelRule(ChannelRuleRequest channelRuleRequest);

  ChannelRuleResponse getChannelRuleBasedOnSourceId(String sourceId);

}
