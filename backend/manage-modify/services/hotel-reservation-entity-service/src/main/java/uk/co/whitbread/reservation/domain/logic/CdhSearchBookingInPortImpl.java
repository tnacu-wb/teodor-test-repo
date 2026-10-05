package uk.co.whitbread.reservation.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;
import uk.co.whitbread.reservation.domain.ports.primary.CdhSearchBookingInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.CdhSearchBookingOutPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhSearchBookingInPortImpl implements CdhSearchBookingInPort {
  private final CdhSearchBookingOutPort cdhSearchBookingOutPort;

  @Override
  public CdhSearchBookingsResponse searchBookingsFromCdh(CdhSearchBookingsRequest cdhSearchBookingsRequest) {

    var cdhSearchBookingsResponse = cdhSearchBookingOutPort.searchBookingsFromCdh(cdhSearchBookingsRequest);

    return cdhSearchBookingsResponse;
  }
}
