package uk.co.whitbread.ohip.domain.logic.utils;

import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.ohip.domain.ports.primary.HotelDetailsInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelDetailsOutPort;

@Slf4j
public class HotelDetailsInPortImpl implements HotelDetailsInPort {

  private final HotelDetailsOutPort hotelDetailsOutPort;

  public HotelDetailsInPortImpl(final HotelDetailsOutPort hotelDetailsOutPort) {
    this.hotelDetailsOutPort = hotelDetailsOutPort;
  }

  @Override
  public List<HotelStatus> getHotelsMigrationStatus(Set<String> hotelIds) {
    log.debug("Entered getHotelsMigrationStatus for hotelIds={}",
        sanitizeInput(hotelIds));
    return this.hotelDetailsOutPort.getHotelsMigrationStatus(hotelIds);
  }

  private String sanitizeInput(Set<String> input) {
    StringBuilder sb = new StringBuilder();
    for (String id : input) {
      if (!sb.isEmpty()) {
        sb.append(", ");
      }
      sb.append(id.replaceAll("[\r\n]", "").replaceAll("[^\\w\\s-]", ""));
    }
    return sb.toString();
  }

}
