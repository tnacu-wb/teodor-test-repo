package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogResponse;

public interface ChangeLogInPort {

  ChangeLogResponse getChangeLog(String hotelId, String reservationId, Integer limit, Integer offset);
}
