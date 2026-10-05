package uk.co.whitbread.reservation.domain.ports.secondary;

import uk.co.whitbread.reservation.domain.model.in.LockRequest;
import uk.co.whitbread.reservation.domain.model.out.LockResponse;

public interface ReservationLockingOutPort {

  LockResponse getLockingWithRetryForHotelId(LockRequest lockRequest);

  LockResponse getLockingForHotelId(LockRequest lockRequest);

}
