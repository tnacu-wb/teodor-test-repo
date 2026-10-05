package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public interface OperaHotelsPostProcessorPort {

  List<Hotel> performOperaHotelsPostProcess(final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet);

  List<Hotel> processHotelResultSetToHotel(final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet);

  boolean isAtleastOneRoomPresentForEachRoomType(final List<Room> roomsList,
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria);

}
