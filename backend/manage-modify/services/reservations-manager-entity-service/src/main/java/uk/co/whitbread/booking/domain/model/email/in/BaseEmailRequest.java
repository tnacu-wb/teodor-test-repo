package uk.co.whitbread.booking.domain.model.email.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseEmailRequest {

  private String email;
  private String hotelId;
  private String bookingReference;

}
