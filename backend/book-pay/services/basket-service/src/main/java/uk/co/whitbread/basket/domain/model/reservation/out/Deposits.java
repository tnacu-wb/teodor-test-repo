package uk.co.whitbread.basket.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Deposits {

  private String paymentReference;
  private CurrencyAmountType postedAmount;

}
