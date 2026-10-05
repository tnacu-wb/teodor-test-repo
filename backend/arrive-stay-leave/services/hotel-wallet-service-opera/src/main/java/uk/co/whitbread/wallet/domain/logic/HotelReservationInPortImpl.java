package uk.co.whitbread.wallet.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.hotel.generated.models.reservation.FindBookingResponseDto;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.wallet.domain.ports.secondary.HotelReservationOutPort;

@Slf4j
@AllArgsConstructor
public class HotelReservationInPortImpl implements HotelReservationInPort {

  private final HotelReservationOutPort hotelReservationOutPort;

  @Override
  public FindBookingResponseDto getBasketReference(WalletRequest walletRequest) {
    String sanitizedBasketRequest = walletRequest.toString().replace('\n', ' ')
            .replace('\r', ' ');
    log.debug("Entered getBasketReference for basket request={}", sanitizedBasketRequest);
    return hotelReservationOutPort.findBooking(walletRequest);
  }
}
