package uk.co.whitbread.ohip.domain.ports.secondary;

import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogResponse;

public interface ChangeLogOutPort {

  ChangeLogResponse getChangeLog(String hotelId, String reservationId, Integer limit, Integer offset);
}
