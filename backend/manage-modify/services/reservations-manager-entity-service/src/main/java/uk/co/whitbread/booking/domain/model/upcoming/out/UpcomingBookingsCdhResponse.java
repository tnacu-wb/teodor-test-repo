package uk.co.whitbread.booking.domain.model.upcoming.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.shared.cdh.model.bookings.UpcomingBooking;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingBookingsCdhResponse {

  private Integer stays;
  private Integer bookings;
  private List<UpcomingBooking> upcomingBooking;
}
