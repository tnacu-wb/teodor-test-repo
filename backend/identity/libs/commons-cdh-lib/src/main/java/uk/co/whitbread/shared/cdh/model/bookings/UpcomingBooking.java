package uk.co.whitbread.shared.cdh.model.bookings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpcomingBooking {
  @JsonProperty("BookingReference")
  private String bookingReference;

  @JsonProperty("HotelCode")
  private String hotelCode;

  @JsonProperty("HotelName")
  private String hotelName;

  @JsonProperty("ArrivalDate")
  private OffsetDateTime arrivalDate;

  @JsonProperty("DepartureDate")
  private OffsetDateTime departureDate;
}
