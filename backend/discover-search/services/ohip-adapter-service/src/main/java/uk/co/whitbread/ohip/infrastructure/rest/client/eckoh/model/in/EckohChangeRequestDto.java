package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohChangeRequestDto {

  private String reservationId;
  private String hotelId;
  private PaymentEckohCardDto paymentCard;

}
