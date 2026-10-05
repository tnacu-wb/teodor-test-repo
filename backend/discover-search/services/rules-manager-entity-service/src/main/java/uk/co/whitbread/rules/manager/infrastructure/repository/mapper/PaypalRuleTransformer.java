package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.springframework.stereotype.Component;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;

@Component
public class PaypalRuleTransformer {

  public RuleStatus statusToStatus(String entityStatus) {
    return RuleStatus.valueOf(entityStatus.toUpperCase());
  }
}
