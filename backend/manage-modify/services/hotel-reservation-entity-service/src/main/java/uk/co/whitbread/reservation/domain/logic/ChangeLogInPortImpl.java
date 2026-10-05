package uk.co.whitbread.reservation.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogResponse;
import uk.co.whitbread.reservation.domain.ports.primary.ChangeLogInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ChangeLogOutPort;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeLogInPortImpl implements ChangeLogInPort {

  private final ChangeLogOutPort changeLogOutPort;

  @Override
  public ChangeLogResponse getChangeLog(String hotelId, String reservationId, Integer limit, Integer offset) {

    var changeLogResponse = changeLogOutPort.getChangeLog(hotelId, reservationId, limit, offset);
    return changeLogResponse;
  }
}
