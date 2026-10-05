package uk.co.whitbread.reservation.domain.ports.primary;

import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.domain.model.in.SearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.domain.model.out.FindBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.ManageBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.SearchBookingsResponse;

public interface ManageBookingInPort {

  ManageBookingResponse getManageBookingInformation(
      String hotelId, String basketReference, String userDateTime, String token,
      BookingChannel bookingChannel, boolean isTokenMandatory,
      ReservationByBasketRefResponse originalReservation);

  FindBookingResponse findBooking(FindBookingRequest findBookingRequest,
      BookingChannel bookingChannel);

  SearchBookingsResponse searchBookings(SearchBookingsRequest searchBookingsRequest);

  void updateUdfc20(UpdateReservationUdfsRequest request);

}
