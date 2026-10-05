package uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceCalendarRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceRequest;

public interface HotelPricePort {

  List<Hotel> getBestPricedHotels(final HotelPriceRequest hotelPriceRequest);

  List<Hotel> getBestPricedHotelsForGivenHotelCode(final String hotelCode,
      final HotelPriceCalendarRequest hotelPriceCalendarRequest);
}
