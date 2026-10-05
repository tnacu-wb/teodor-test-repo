package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public interface HotelAvailabilitiesPersistencePostProcessorPort {

  List<Hotel> performPostProcessHotelAvailabilities(final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet);

  List<Hotel> processHotelResultSetToHotel(final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet);

  List<RatePlan> mapRateFromResultSetToRatePlan(final String hotelCode,
      final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelResultSetList);

  List<Room> mapRoomFromResultSetToRoom(final String hotelCode, final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelResultSetList);

  boolean isQuantityLessThanRoomsRequested(List<HotelAvailabilitiesResultSet> hotelAvailList,
      Long roomTypeCountFromSearchCriteria,
      String roomType);

  List<Hotel> populateLimitedAvailabilityAndEuroCurrency(final List<Hotel> hotels,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet);

  List<Hotel> populateLimitedAvailability(final List<Hotel> hotels,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet);

}
