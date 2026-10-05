package uk.co.whitbread.booking.domain.model.upcoming.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpcomingBookingRequest {

  private String language;
  private String country;
  private String channel;
  private String subchannel;
}
