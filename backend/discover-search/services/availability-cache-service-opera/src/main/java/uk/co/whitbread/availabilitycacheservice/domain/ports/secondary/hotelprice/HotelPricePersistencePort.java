package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceCalendarRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceRequest;

public interface HotelPricePersistencePort {

  List<Hotel> getBestPriceForHotels(final HotelPriceRequest hotelPriceRequest);

  List<Hotel> getBestPricesForGivenHotelCode(final String hotelCode,
      final HotelPriceCalendarRequest hotelPriceCalendarRequest);
}
