package uk.co.whitbread.rules.agent.domain.ports.primary;

import uk.co.whitbread.rules.agent.domain.model.in.VatRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.VatRuleResponse;

public interface VatRuleInPort {

  VatRuleResponse getVatRule(VatRuleRequest vatRuleRequest);

}
