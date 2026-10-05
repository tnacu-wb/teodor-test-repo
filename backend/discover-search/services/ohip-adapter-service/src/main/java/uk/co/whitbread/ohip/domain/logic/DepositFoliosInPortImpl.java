package uk.co.whitbread.ohip.domain.logic;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.ohip.domain.ports.primary.DepositFoliosInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelReservationOutPort;

@Slf4j
@RequiredArgsConstructor
public class DepositFoliosInPortImpl implements DepositFoliosInPort {

  private final HotelReservationOutPort hotelReservationOhipPort;

  @Override
  public DepositFoliosResponse getDepositFolios(String hotelId, Set<String> reservationIds) {
    return hotelReservationOhipPort.getDepositFolioForReservations(hotelId, reservationIds);
  }

  @Override
  public void createDepositFolios(DepositFoliosResponse depositFoliosResponse) {
    hotelReservationOhipPort.createDepositFolios(depositFoliosResponse);
  }
}




