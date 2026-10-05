package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import java.util.LinkedHashSet;
import java.util.Set;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.los.rules.LosRestriction;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public interface LosRestrictionRulesEvaluator {
  
  Set<LosRestrictionRule> registeredRules = new LinkedHashSet<>();

  static void registerRule(final LosRestrictionRule rule) {
    registeredRules.add(rule);
  }

  //below method only for JUnits
  static void clearRules() {
    registeredRules.clear();
  }

  LosRestriction evaluateRestrictionRules(final RatePlan hotelRate, final SearchCriteria searchCriteria);
}
