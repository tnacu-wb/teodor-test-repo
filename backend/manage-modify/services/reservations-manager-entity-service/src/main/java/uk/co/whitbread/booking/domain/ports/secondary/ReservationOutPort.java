package uk.co.whitbread.booking.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.BaseEmailRequest;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.in.HotelInformationRequest;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.BookingRoom;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.model.information.out.HotelInformationResponse;
import uk.co.whitbread.booking.domain.model.information.out.UpcomingBookings;
import uk.co.whitbread.booking.domain.model.invoice.DownloadBookingInvoicesRequest;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownloadResponse;

public interface ReservationOutPort {

  BookingInfoResponse getOperaBookingInformation(BookingInfoRequest bookingInfoRequest,
      BookingChannel reservationChannel);

  CancelBookingResponse cancelBooking(CancelBookingRequest cancelBookingRequest);

  void sendBookingConfirmationOrInvoiceEmail(BaseEmailRequest bookingReference);

  void findBooking(BookingInfoRequest bookingInfoRequest, BookingChannel bookingChannel, boolean isPastBooking);

  List<BookingRoom> getReservationInformationAuth(String token, String basketReference);

  HotelInformationResponse getHotelInformation(HotelInformationRequest hotelInformationRequest);

  List<UpcomingBookings> getStayDates(String token, String basketReference);

  InvoiceDownloadResponse downloadBookingInvoices(DownloadBookingInvoicesRequest request);
}
