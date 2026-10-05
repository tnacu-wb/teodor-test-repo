package uk.co.whitbread.rules.agent.domain.ports.primary;

import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRuleResponse;

public interface RateSuppressionRuleInPort {

  RateSuppressionRuleResponse getRateSuppressionRule();

}
