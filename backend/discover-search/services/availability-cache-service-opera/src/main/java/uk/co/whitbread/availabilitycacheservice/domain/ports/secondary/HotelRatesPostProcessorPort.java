package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public interface HotelRatesPostProcessorPort {

  boolean isAllDaysRatesReturned(final SearchCriteria searchCriteria,
      List<HotelAvailabilitiesResultSet> hotelResultSetWithRates,
      final String rateClassification, final String hotelCode);

}
