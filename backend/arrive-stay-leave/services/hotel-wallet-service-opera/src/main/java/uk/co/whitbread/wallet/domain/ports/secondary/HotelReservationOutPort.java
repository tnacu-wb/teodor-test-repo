package uk.co.whitbread.wallet.domain.ports.secondary;

import uk.co.whitbread.hotel.generated.models.reservation.FindBookingResponseDto;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.domain.model.out.ReservationDetails;

public interface HotelReservationOutPort {

  FindBookingResponseDto findBooking(WalletRequest walletRequest);

  ReservationDetails getReservationDetails(String basketReference, String earlyCheckIn);
}
