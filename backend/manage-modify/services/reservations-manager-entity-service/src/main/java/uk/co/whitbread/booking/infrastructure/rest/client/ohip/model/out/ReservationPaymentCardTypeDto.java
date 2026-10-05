package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ReservationPaymentCardTypeDto {
  private String paymentMethod;
  private Integer folioView;
}
