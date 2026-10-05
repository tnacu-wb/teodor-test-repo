package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.springframework.stereotype.Component;
import uk.co.whitbread.rules.manager.domain.model.in.CountryCode;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;

@Component
public class AmendmentRuleTransformer {

  public RuleStatus statusToStatus(String entityStatus) {
    return RuleStatus.valueOf(entityStatus.toUpperCase());
  }

  public CountryCode countryCodeToCountryCode(String entityCountryCode) {
    return CountryCode.valueOf(entityCountryCode.toUpperCase());
  }
}
