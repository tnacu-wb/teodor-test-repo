package uk.co.whitbread.dashboard.infrastructure.rest.client.cdhadapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.CdhReservationSearchDto;
import uk.co.whitbread.dashboard.domain.ports.secondary.CdhReservationsOutPort;
import uk.co.whitbread.dashboard.infrastructure.rest.client.cdhadapter.service.CdhAdapterClient;

@RequiredArgsConstructor
@Component
@Slf4j
public class CdhReservationOutPortImpl implements CdhReservationsOutPort {

  private final CdhAdapterClient cdhAdapterClient;

  @Override
  public CdhReservationSearchDto retrieveBooking(final String reservationId) {
    return cdhAdapterClient.retrieveBooking(reservationId);
  }
}