package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.common.CommonHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public interface LosRestrictionPort<H extends CommonHotel> {

  List<H> applyLosRestrictions(final SearchCriteria criteria, final List<H> hotels);

  boolean isLosApplicable(RatePlan ratePlan, SearchCriteria criteria);

}
