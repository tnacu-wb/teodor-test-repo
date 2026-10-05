package uk.co.whitbread.reservation.domain.ports.primary;

import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;

public interface CdhSearchBookingInPort {

  CdhSearchBookingsResponse searchBookingsFromCdh(
      CdhSearchBookingsRequest cdhSearchBookingsRequest);
}
