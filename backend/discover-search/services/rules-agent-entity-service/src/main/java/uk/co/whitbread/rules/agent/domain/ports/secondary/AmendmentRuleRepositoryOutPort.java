package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRule;

public interface AmendmentRuleRepositoryOutPort extends RuleEngineRepositoryOutPort<AmendmentRule> {

  Optional<AmendmentRule> findRule(String rateType, String countryCode);
}
