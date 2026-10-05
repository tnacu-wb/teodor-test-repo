package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReservationCityTaxForLeisureResponseDto {

  private String hotelHasCityTaxForLeisure;
}
