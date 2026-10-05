package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRateInformationRequestDto {

  private String country;
  private String language;
  private String hotelId;
  private String channel;
}
