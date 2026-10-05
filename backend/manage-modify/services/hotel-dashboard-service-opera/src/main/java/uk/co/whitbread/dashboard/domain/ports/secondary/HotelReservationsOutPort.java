package uk.co.whitbread.dashboard.domain.ports.secondary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import uk.co.whitbread.dashboard.domain.model.in.FindBookingResponse;
import uk.co.whitbread.dashboard.domain.model.in.ManageBookingResponse;

public interface HotelReservationsOutPort {

  FindBookingResponse findBooking(final String lastName, final LocalDate arrivalDate, final String bookingReference,
      final String language);

  ManageBookingResponse getManageBookingInfo(final String basketReference, final String hotelId, final String token,
      final String language, LocalDateTime userDateTime);

}