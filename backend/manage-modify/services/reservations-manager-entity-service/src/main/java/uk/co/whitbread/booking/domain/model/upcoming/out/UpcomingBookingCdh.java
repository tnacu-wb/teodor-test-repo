package uk.co.whitbread.booking.domain.model.upcoming.out;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingBookingCdh {

  private String bookingReference;
  private String hotelCode;
  private String hotelName;
  private OffsetDateTime arrivalDate;
  private OffsetDateTime departureDate;
}
