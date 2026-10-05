package uk.co.whitbread.rules.agent.domain.ports.secondary;

import uk.co.whitbread.rules.agent.domain.model.out.PaypalRule;


public interface PaypalRuleRepositoryOutPort extends RuleEngineRepositoryOutPort<PaypalRule> {

  Boolean getPaypalHasAccess(String channelId, String country, String hotelId);
}
