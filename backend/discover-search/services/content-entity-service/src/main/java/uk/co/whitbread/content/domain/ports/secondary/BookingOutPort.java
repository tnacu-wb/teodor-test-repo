package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.domain.model.booking.in.RateInformationRequest;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.domain.model.booking.out.RateInformation;

public interface BookingOutPort {

  BookingInformation getBookingInformation(BookingInformationRequest bookingInformationRequest);

  RateInformation getRateInformation(RateInformationRequest rateInformationRequest);

  RateInformation getHotelRateInformation(RateInformationRequest rateInformationRequest);

}
