package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.time.LocalDate;
import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderRate;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.CalendarPriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;

public interface PriceFinderLocationAvailabilititesOutPort {

  List<PriceFinderOperaHotelAvailabilities> getAvailabilitiesByLocation(PriceFinderLocationSearchCriteria criteria,
      LocalDate dateRangeEnd);

  CalendarPriceFinderOperaHotelAvailabilities getAvailabilitiesByLocationForCalendar(
      CalendarPriceFinderLocationSearchCriteria criteria);

  PriceFinderRate getLowestMonthlyRateByLocation(PriceFinderLocationSearchCriteria criteria);
}
