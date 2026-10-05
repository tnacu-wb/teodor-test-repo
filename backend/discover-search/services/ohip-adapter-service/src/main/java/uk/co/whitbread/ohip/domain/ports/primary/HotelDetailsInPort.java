package uk.co.whitbread.ohip.domain.ports.primary;

import java.util.List;
import java.util.Set;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;

public interface HotelDetailsInPort {

  List<HotelStatus> getHotelsMigrationStatus(final Set<String> hotelIds);

}
