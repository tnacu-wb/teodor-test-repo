package uk.co.whitbread.rules.agent.domain.ports.primary;

import uk.co.whitbread.rules.agent.domain.model.out.BaseRateRuleResponse;

public interface BaseRateRuleInPort {
  
  BaseRateRuleResponse getBaseRate(String ratePlanCode);
}
