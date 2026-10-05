package uk.co.whitbread.shared.cdh.model.bookings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpcomingBookingsResponse {
  @JsonProperty("Stays")
  private Integer stays;

  @JsonProperty("Bookings")
  private Integer bookings;

  @JsonProperty("UpcommingBooking")
  private List<UpcomingBooking> upcomingBooking;
}
