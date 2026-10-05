package uk.co.whitbread.booking.domain.ports.secondary;

import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsCdhResponse;

public interface CdhOutPort {

  UpcomingBookingsCdhResponse getUpcomingBookings(String companyId, String employeeId, String email);
}
