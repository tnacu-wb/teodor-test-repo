package uk.co.whitbread.ohip.domain.logic;

import static uk.co.whitbread.ohip.domain.utils.SanitizingUtils.sanitize;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogResponse;
import uk.co.whitbread.ohip.domain.ports.primary.ChangeLogInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.ChangeLogOutPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChangeLogInPortImpl implements ChangeLogInPort {

  private final ChangeLogOutPort changeLogOutPort;

  @Override
  public ChangeLogResponse getChangeLog(String hotelId, String reservationId, Integer limit, Integer offset) {
    log.debug("Entered getChangeLog for hotelId {} and reservationId {}",
        sanitize(hotelId), sanitize(reservationId));
    return changeLogOutPort.getChangeLog(hotelId, reservationId, limit, offset);
  }
}
