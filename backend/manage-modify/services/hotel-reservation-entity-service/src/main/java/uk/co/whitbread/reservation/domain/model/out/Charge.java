package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Charge {

  private String transactionCode;
  private Integer postingQuantity;
  private String postingReference;
  private CurrencyAmount currencyAmount;

}
