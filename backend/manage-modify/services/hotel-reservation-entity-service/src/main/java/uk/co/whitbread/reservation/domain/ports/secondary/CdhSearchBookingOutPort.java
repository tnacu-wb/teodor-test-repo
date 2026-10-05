package uk.co.whitbread.reservation.domain.ports.secondary;

import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;

public interface CdhSearchBookingOutPort {

  CdhSearchBookingsResponse searchBookingsFromCdh(
      CdhSearchBookingsRequest cdhSearchBookingsRequest);
}
