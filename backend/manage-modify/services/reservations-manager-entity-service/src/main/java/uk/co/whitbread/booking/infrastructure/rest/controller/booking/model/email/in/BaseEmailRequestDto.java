package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.email.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEmailRequestDto {

  private String email;
  private String hotelId;
  private String bookingReference;
}
