package uk.co.whitbread.reservation.domain.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Deposits {
  private String paymentReference;
  private CurrencyAmountType postedAmount;
}