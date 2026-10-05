package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseOperaEmailRequestDto {

  private String email;
  private String hotelId;
  private String bookingReference;
}
