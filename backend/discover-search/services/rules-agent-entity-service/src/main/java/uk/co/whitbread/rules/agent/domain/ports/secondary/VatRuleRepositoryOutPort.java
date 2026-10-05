package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.rules.agent.domain.model.in.VatRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.VatRule;

public interface VatRuleRepositoryOutPort extends RuleEngineRepositoryOutPort<VatRule> {

  List<VatRule> findVatRules(VatRuleRequest vatRuleRequest);

}
