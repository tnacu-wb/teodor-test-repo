package uk.co.whitbread.content.domain.ports.primary;

import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.domain.model.booking.in.RateInformationRequest;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.domain.model.booking.out.RateInformation;

public interface BookingInPort {

  BookingInformation getBookingInformation(BookingInformationRequest bookingInformationRequest);

  RateInformation getRateInformation(RateInformationRequest rateInformationRequest);

  RateInformation getHotelRateInformation(RateInformationRequest rateInformationRequest);
}
