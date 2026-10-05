package uk.co.whitbread.ohip.domain.ports.primary;

import java.util.Set;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFoliosResponse;

public interface DepositFoliosInPort {

  DepositFoliosResponse getDepositFolios(String hotelId, Set<String> reservationIds);

  void createDepositFolios(DepositFoliosResponse depositFoliosResponse);

}

