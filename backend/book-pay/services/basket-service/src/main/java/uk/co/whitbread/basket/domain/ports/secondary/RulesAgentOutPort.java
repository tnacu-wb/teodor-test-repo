package uk.co.whitbread.basket.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.basket.domain.model.rules.out.VatRuleResponse;

public interface RulesAgentOutPort {

  BusinessAllowanceRuleResponse getBusinessAllowanceRules();

  VatRuleResponse getVatCodes(String vatRegion, List<String> pkgCodeArr);

}
