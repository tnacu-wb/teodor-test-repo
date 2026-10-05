package uk.co.whitbread.reservation.domain.ports.secondary;

import uk.co.whitbread.reservation.domain.model.out.ChangeLogResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.model.out.ChangeLogResponseDto;

public interface ChangeLogOutPort {

  ChangeLogResponse getChangeLog(String hotelId, String reservationId, Integer limit, Integer offset);
}
