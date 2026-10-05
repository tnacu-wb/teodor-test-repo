package uk.co.whitbread.wallet.domain.ports.primary;

import uk.co.whitbread.hotel.generated.models.reservation.FindBookingResponseDto;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;

public interface HotelReservationInPort {

  FindBookingResponseDto getBasketReference(WalletRequest walletRequest);

}
