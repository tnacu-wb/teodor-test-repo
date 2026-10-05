package uk.co.whitbread.payments.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentAction {
  private ChargeType chargeType;
  private Price price;
  private Integer order;
}