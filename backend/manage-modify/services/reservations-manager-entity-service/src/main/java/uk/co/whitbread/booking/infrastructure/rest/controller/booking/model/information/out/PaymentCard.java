package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCard {

  private String cardType;
}
