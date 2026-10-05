package uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary;

import java.time.LocalDate;
import java.util.Set;

public interface OperaHotelAvailabilityRefreshOutPort {

  void refreshHotelAvailabilities(final Set<String> hotelCodes, final LocalDate startDate, final LocalDate endDate);

}
