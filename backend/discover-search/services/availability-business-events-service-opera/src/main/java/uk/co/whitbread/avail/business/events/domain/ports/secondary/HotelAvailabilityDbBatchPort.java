package uk.co.whitbread.avail.business.events.domain.ports.secondary;

import java.math.BigInteger;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;

public interface HotelAvailabilityDbBatchPort {

  void processDbBatchUpdate(final EventHeader eventHeader, final String appKey);

  BigInteger getProcessedOffset(final String appKey);
}
