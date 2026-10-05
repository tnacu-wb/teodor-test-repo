package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public interface LosRestrictionRule {

  boolean isApplicable(final RatePlan hotelRate, final SearchCriteria searchCriteria);

  LosRestrictionName getRuleName();

  void registerRule();
}
