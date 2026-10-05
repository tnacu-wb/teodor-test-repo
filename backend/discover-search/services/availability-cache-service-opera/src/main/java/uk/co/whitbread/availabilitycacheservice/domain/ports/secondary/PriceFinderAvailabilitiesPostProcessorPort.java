package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import java.util.Set;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;

public interface PriceFinderAvailabilitiesPostProcessorPort {

  List<PriceFinderOperaHotelAvailabilities> processHotelResultSet(
          final List<PriceFinderResultSet> priceFinderResultSets,
          final PriceFinderSearchCriteria priceFinderSearchCriteria);

  // Overloaded method to support allowedRoomTypes filtering
  List<PriceFinderOperaHotelAvailabilities> processHotelResultSet(
          final List<PriceFinderResultSet> priceFinderResultSets,
          final PriceFinderSearchCriteria priceFinderSearchCriteria,
          final Set<String> allowedRoomTypes);

  CalendarPriceFinderOperaHotelAvailabilities processHotelResultSetForCalendar(
      final List<PriceFinderResultSet> filteredPriceFinderResultSet,
      final PriceFinderSearchCriteria priceFinderSearchCriteria);
}
