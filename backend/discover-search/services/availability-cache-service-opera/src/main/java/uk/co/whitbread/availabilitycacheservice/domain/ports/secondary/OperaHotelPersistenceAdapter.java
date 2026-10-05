package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import java.util.Set;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;

public interface OperaHotelPersistenceAdapter {

  List<Hotel> getHotelAvailabilitiesForOpera(
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria);

  String[] getOperaRoomTypes(final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
                             final Set<String> operaRoomTypes, final List<String> validRoomTypes);
}
