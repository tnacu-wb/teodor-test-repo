package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRule;

public interface RateSuppressionRuleRepositoryOutPort extends
    RuleEngineRepositoryOutPort<RateSuppressionRule> {

  List<RateSuppressionRule> findRateSuppressionRule();
}
