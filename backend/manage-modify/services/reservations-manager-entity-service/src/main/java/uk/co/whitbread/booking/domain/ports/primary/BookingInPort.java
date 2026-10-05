package uk.co.whitbread.booking.domain.ports.primary;

import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.ResendConfirmationEmailRequest;
import uk.co.whitbread.booking.domain.model.email.in.ResendInvoiceEmailRequest;
import uk.co.whitbread.booking.domain.model.history.in.BookingRequest;
import uk.co.whitbread.booking.domain.model.history.out.BookingResponse;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.model.invoice.DownloadBookingInvoicesRequest;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownloadResponse;
import uk.co.whitbread.booking.domain.model.upcoming.in.UpcomingBookingRequest;
import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsResponse;

public interface BookingInPort {

  BookingResponse getBookings(String authorization, BookingRequest bookingRequest, BookingChannel bookingChannel);

  BookingInfoResponse getBookingInformation(BookingInfoRequest bookingInfoRequest, BookingChannel bookingChannel);

  CancelBookingResponse cancelBooking(CancelBookingRequest cancelBookingRequest);

  void sendBookingConfirmationEmail(ResendConfirmationEmailRequest request);

  void resendBookingInvoiceEmail(
      ResendInvoiceEmailRequest request);

  UpcomingBookingsResponse getUpcomingBookings(UpcomingBookingRequest request, String authorization);

  InvoiceDownloadResponse downloadBookingInvoices(String authorization, DownloadBookingInvoicesRequest request);
}
