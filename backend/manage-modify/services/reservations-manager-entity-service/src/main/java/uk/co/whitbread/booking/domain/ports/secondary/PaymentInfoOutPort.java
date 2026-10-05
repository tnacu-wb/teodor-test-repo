package uk.co.whitbread.booking.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.booking.domain.model.history.out.Booking;

public interface PaymentInfoOutPort {


  List<String> getPibaCpReservations(List<Booking> bookings, boolean excludePibaCP,
      boolean excludePibaCNP);
}
