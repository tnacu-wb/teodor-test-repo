package uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary;

import java.time.LocalDate;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionInput;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionResponse;

public interface RateRestrictionOutPort {
    RateRestrictionResponse searchRateRestrictionCriteria(RateRestrictionInput rateRestrictionInput);

    RateRestrictionResponse getRateRestrictions(final String hotelCode, final LocalDate startDate, final LocalDate endDate);
}
