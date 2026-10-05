package uk.co.whitbread.booking.domain.ports.secondary;

import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.ResendConfirmationEmailRequest;
import uk.co.whitbread.booking.domain.model.email.in.ResendInvoiceEmailRequest;
import uk.co.whitbread.booking.domain.model.history.in.BookingRequest;
import uk.co.whitbread.booking.domain.model.history.out.BookingResponse;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;

public interface BookingOutPort {

  BookingResponse getBookings(String authorization, BookingRequest bookingRequest, BookingChannel bookingChannel);

  BookingInfoResponse getBookingInformation(BookingInfoRequest bookingInfoRequest, BookingChannel bookingChannel);

  CancelBookingResponse cancelBooking(CancelBookingRequest cancelBookingRequest, BookingChannel bookingChannel);

  void sendBookingConfirmationEmail(ResendConfirmationEmailRequest request);

  void resendBookingInvoiceEmail(
      ResendInvoiceEmailRequest request, String authorization,
      BookingChannel bookingChannel
  );
}
