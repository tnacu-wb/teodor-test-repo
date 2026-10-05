package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.in;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingInfoRequestDto {

  private String hotelId;
  private String bookingReference;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String arrival;

  private String surname;
  private String country;
  private String language;
  private String sourceSystem;
  private String token;
}
