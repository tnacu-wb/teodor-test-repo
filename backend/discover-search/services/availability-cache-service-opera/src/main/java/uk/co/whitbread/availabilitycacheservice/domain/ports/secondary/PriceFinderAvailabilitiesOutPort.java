package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.time.LocalDate;
import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.HotelRoomRateInfo;

public interface PriceFinderAvailabilitiesOutPort {

  List<HotelRoomRateInfo> getMinRateHotelAvailabilities(final List<String> operaHotelCodes, final LocalDate arrivalDate,
      final LocalDate departureDate);
}
