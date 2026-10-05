package uk.co.whitbread.booking.domain.ports.secondary;

import java.util.List;
import java.util.Map;
import uk.co.whitbread.booking.domain.model.history.out.Booking;

public interface HotelInfoOutPort {

  Map<String, String> getHotelInfo(List<Booking> bookings);

}
