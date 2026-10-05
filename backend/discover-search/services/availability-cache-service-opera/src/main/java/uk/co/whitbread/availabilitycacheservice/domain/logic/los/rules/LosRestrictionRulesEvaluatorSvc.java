package uk.co.whitbread.availabilitycacheservice.domain.logic.los.rules;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName;
import uk.co.whitbread.availabilitycacheservice.domain.model.los.rules.LosRestriction;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRule;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRulesEvaluator;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
@Service
public class LosRestrictionRulesEvaluatorSvc implements LosRestrictionRulesEvaluator {

  @Override
  public LosRestriction evaluateRestrictionRules(final RatePlan hotelRate, final SearchCriteria searchCriteria) {
    log.debug("Evaluating LOS rules for rate plan : {}", hotelRate);
    LosRestriction restrictionToApply = LosRestriction.builder().losRule(LosRestrictionName.NO_RESTRICTIONS).build();
    for (final LosRestrictionRule losRule : registeredRules) {
      if (losRule.isApplicable(hotelRate, searchCriteria)) {
        restrictionToApply.setLosRule(losRule.getRuleName());
        break;
      }
    }

    return restrictionToApply;
  }

}
