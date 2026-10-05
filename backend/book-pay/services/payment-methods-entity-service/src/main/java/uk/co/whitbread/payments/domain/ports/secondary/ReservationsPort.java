package uk.co.whitbread.payments.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.payments.domain.model.out.BasketReservation;
import uk.co.whitbread.payments.domain.model.out.DepositsResponse;
import uk.co.whitbread.payments.domain.model.out.Reservation;

public interface ReservationsPort {

  /**
   * Finds reservations by basket reference.
   *
   * @param basketReference the basket reference
   * @param useCache if true, returns cached reservations if available; if false, fetches from API and updates cache
   * @return the reservation
   */
  Reservation findReservations(String basketReference, boolean useCache);

  /**
   * Finds basket reservations by basket reference.
   *
   * @param basketReference the basket reference
   * @return the basket reservation
   */
  BasketReservation findBasketReservations(String basketReference);

  DepositsResponse getDepositFolios(String hotelId, List<String> reservationIds);
}
