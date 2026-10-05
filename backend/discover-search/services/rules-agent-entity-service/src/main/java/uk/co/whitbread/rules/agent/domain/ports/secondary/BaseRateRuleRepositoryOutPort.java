package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.rules.agent.domain.model.out.BaseRateRule;

public interface BaseRateRuleRepositoryOutPort extends RuleEngineRepositoryOutPort<BaseRateRule> {
  
  Optional<BaseRateRule> getBaseRate(String ratePlanCode);
}
