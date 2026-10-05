package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Deposits {
  private String paymentReference;
  private CurrencyAmountType postedAmount;
}