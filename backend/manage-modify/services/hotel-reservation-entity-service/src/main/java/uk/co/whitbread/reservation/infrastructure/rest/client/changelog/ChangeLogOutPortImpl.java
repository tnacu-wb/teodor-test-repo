package uk.co.whitbread.reservation.infrastructure.rest.client.changelog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.ChangeLogOutPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.changelog.mapper.ChangeLogResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeLogOutPortImpl implements ChangeLogOutPort {

  private final OhipAdapterClient ohipAdapterClient;
  private final ChangeLogResponseOhipMapper changeLogResponseOhipMapper;

  @Override
  public ChangeLogResponse getChangeLog(String hotelId, String reservationId, Integer limit, Integer offset) {
    var changeLogResponseDto =
        ohipAdapterClient.getChangeLog(hotelId, reservationId, limit, offset);
    var response =  changeLogResponseOhipMapper.toModel(changeLogResponseDto);
    response.getActivityLog().getActivityLog().forEach(log -> {
      var splitDateTime = log.getLogDate().split(",");

      log.setUser(log.getLogUserName());
      log.setDate(splitDateTime[0].trim());
      log.setTime(splitDateTime[1].trim());
    });
    return response;
  }
}
