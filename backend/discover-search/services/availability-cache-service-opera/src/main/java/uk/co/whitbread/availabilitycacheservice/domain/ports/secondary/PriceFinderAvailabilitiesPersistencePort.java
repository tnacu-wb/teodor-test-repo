package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;

public interface PriceFinderAvailabilitiesPersistencePort {

  List<PriceFinderResultSet> getLowestPricesByHotels(
      final PriceFinderSearchCriteria priceFinderSearchCriteria);
}
