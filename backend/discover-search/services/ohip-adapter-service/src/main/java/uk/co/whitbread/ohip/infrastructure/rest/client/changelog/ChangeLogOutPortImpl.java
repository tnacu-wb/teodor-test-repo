package uk.co.whitbread.ohip.infrastructure.rest.client.changelog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.ChangeLogOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.changelog.mapper.ChangeLogResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChangeLogOutPortImpl implements ChangeLogOutPort {

  private final OhipReservationClient ohipReservationClient;
  private final ChangeLogResponseOhipMapper changeLogResponseOhipMapper;

  @Override
  public ChangeLogResponse getChangeLog(String hotelId, String reservationId, Integer limit, Integer offset) {

    final var activityLog = ohipReservationClient.getActivityLog(hotelId, reservationId, limit, offset);
    return changeLogResponseOhipMapper.toDomainModel(activityLog);
  }
}
