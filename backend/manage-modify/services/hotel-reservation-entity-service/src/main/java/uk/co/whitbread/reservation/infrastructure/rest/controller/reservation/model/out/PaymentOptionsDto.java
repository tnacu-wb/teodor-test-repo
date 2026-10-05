package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOptionsDto {

  private Boolean payOnArrival;
  private Boolean payNow;
}
