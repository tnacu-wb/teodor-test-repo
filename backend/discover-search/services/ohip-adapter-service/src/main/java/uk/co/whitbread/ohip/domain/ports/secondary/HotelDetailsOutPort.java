package uk.co.whitbread.ohip.domain.ports.secondary;

import java.util.List;
import java.util.Set;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;

public interface HotelDetailsOutPort {

  List<HotelStatus> getHotelsMigrationStatus(Set<String> hotelIds);
}
