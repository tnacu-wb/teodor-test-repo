package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationBasketOptionsResponseDto {

  private String reference;
  private String paymentOption;
}
