package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.rules.agent.domain.model.out.BusinessAllowanceRule;

public interface BusinessAllowanceRuleRepositoryOutPort
    extends RuleEngineRepositoryOutPort<BusinessAllowanceRule> {

  List<BusinessAllowanceRule> findRules();
}
