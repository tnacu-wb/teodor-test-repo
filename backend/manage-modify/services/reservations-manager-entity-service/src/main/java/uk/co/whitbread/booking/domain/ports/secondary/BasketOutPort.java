package uk.co.whitbread.booking.domain.ports.secondary;

import java.util.Map;
import java.util.Set;
import uk.co.whitbread.booking.domain.model.history.out.BasketStatus;

public interface BasketOutPort {
  Map<String, BasketStatus> getBasketStatusesForBookingRefs(Set<String> bookingRefs);

  Map<String, Set<String>> getBasketSourceIdsForBookingRefs(Set<String> bookingRefs);
}
