package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.CalendarPriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;

public interface PriceFinderHotelAvailabilitiesPort {

  List<PriceFinderOperaHotelAvailabilities> getLowestPricesByHotel(
          final PriceFinderSearchCriteria priceFinderSearchCriteria);

  PriceFinderHotelAvailabilities getLowestPricesByLocation(PriceFinderLocationSearchCriteria criteria);

  PriceFinderHotelAvailabilities getLowestPricesByLocationWithRoomTypeSubstitution(
          PriceFinderLocationSearchCriteria criteria, List<String> filterByRoomType);

  CalendarPriceFinderHotelAvailabilities getLowestPricesByLocationForCalendar(
      CalendarPriceFinderLocationSearchCriteria criteria);

}
