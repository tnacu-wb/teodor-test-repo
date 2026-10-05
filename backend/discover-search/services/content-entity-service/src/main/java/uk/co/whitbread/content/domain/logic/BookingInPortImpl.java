package uk.co.whitbread.content.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.domain.model.booking.in.RateInformationRequest;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.domain.model.booking.out.RateInformation;
import uk.co.whitbread.content.domain.ports.primary.BookingInPort;
import uk.co.whitbread.content.domain.ports.secondary.BookingOutPort;

@Slf4j
@RequiredArgsConstructor
public class BookingInPortImpl implements BookingInPort {

  private final BookingOutPort bookingOutPort;

  @Override
  public BookingInformation getBookingInformation(
      BookingInformationRequest bookingInformationRequest) {
    return bookingOutPort.getBookingInformation(bookingInformationRequest);
  }

  @Override
  public RateInformation getRateInformation(
      RateInformationRequest rateInformationRequest) {
    return bookingOutPort.getRateInformation(rateInformationRequest);
  }

  @Override
  public RateInformation getHotelRateInformation(RateInformationRequest rateInformationRequest) {
    return bookingOutPort.getHotelRateInformation(rateInformationRequest);
  }
}
