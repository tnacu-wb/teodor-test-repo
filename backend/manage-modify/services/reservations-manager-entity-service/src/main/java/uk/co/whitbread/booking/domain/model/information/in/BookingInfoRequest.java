package uk.co.whitbread.booking.domain.model.information.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BookingInfoRequest {

  private String hotelId;
  private String bookingReference;

  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
  private String arrival;

  private String surname;
  private String language;
  private String country;
  private String sourceSystem;
  private String token;
}
