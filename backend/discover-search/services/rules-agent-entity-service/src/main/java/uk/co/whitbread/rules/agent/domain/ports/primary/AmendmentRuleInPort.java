package uk.co.whitbread.rules.agent.domain.ports.primary;

import uk.co.whitbread.rules.agent.domain.model.in.AmendmentRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRuleResponse;

public interface AmendmentRuleInPort {

  AmendmentRuleResponse getAmendmentRule(AmendmentRuleRequest domainAmendmentRuleRequest);
}
