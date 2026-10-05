package uk.co.whitbread.ohip.infrastructure.rest.client.udfs;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import uk.co.whitbread.ohip.domain.model.udfs.in.CharacterUdf;
import uk.co.whitbread.ohip.domain.ports.secondary.UdfsOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.udfs.mapper.UpdateReservationOverrideUdfsRequestOhipMapper;

@RequiredArgsConstructor
@Slf4j
public class UdfsOutPortImpl implements UdfsOutPort {

  private final OhipReservationClient ohipReservationClient;
  private final UpdateReservationOverrideUdfsRequestOhipMapper mapper;

  @Override
  public void updateUdfs(String hotelId, Set<String> reservationIds, List<CharacterUdf> udfs) {
    Flux.fromIterable(reservationIds)
        .flatMap(res -> ohipReservationClient.sendChangeReservationRequest(
            hotelId,
            res,
            mapper.toDto(hotelId, udfs)))
        .collectList()
        .block();
  }
}
