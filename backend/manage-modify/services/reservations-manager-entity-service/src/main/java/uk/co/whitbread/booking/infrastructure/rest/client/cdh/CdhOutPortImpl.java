package uk.co.whitbread.booking.infrastructure.rest.client.cdh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsCdhResponse;
import uk.co.whitbread.booking.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.booking.infrastructure.rest.client.cdh.mapper.UpcomingBookingsCdhResponseMapper;

@Slf4j
@RequiredArgsConstructor
public class CdhOutPortImpl implements CdhOutPort {

  private final CdhClient cdhClient;
  private final UpcomingBookingsCdhResponseMapper upcomingBookingsResponseMapper;

  @Override
  public UpcomingBookingsCdhResponse getUpcomingBookings(String companyId, String employeeId,
        String email) {
    var upcomingBookings = cdhClient.getUpcomingBookings(companyId, employeeId, email);

    return upcomingBookingsResponseMapper.toModel(upcomingBookings);
  }
}
