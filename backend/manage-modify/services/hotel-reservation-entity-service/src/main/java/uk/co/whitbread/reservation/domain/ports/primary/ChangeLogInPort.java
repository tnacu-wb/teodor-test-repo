package uk.co.whitbread.reservation.domain.ports.primary;

import uk.co.whitbread.reservation.domain.model.out.ChangeLogResponse;

public interface ChangeLogInPort {

  ChangeLogResponse getChangeLog(String hotelId, String reservationId, Integer limit, Integer offset);
}
